package com.saas.legit.module.admin.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.admin.dto.*;
import com.saas.legit.module.appointment.repository.AppointmentRepository;
import com.saas.legit.module.identity.model.AccountStatus;
import com.saas.legit.module.identity.model.IdentityDocument;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.IdentityDocumentRepository;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.model.Specialty;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import com.saas.legit.module.marketplace.repository.ReviewRepository;
import com.saas.legit.module.marketplace.repository.SpecialtyRepository;
import com.saas.legit.module.marketplace.dto.SpecialtyResponse;
import com.saas.legit.module.document.model.DocumentTemplate;
import com.saas.legit.module.document.repository.DocumentTemplateRepository;
import com.saas.legit.module.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final IdentityDocumentRepository identityDocumentRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DocumentTemplateRepository documentTemplateRepository;

    // ── DASHBOARD METRICS ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardMetrics() {
        long totalUsers = userRepository.count();
        long totalLawyers = userRepository.countByRole_NameRol("LAWYER");
        long totalClients = userRepository.countByRole_NameRol("CLIENT");
        long pendingVerifications = lawyerProfileRepository.countByVerificationStatus(
                LawyerProfile.VerificationStatus.PENDING);
        long totalAppointments = appointmentRepository.count();
        long totalReviews = reviewRepository.count();

        OffsetDateTime sevenDaysAgo = OffsetDateTime.now().minusDays(7);
        long recentRegistrations = userRepository.countByCreatedAtAfter(sevenDaysAgo);

        BigDecimal monthlyRevenue = paymentRepository.sumSucceededPaymentsCurrentMonth()
                .orElse(BigDecimal.ZERO);

        return new AdminDashboardResponse(
                totalUsers,
                totalLawyers,
                totalClients,
                pendingVerifications,
                totalAppointments,
                totalReviews,
                recentRegistrations,
                monthlyRevenue
        );
    }

    // ── USER MANAGEMENT ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<AdminUserListResponse> getUsers(String search, String role, String status, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(search, role, status, pageable);

        // Extraer IDs de abogados en un solo paso
        List<Long> lawyerUserIds = users.stream()
                .filter(u -> u.hasRole("LAWYER"))
                .map(User::getIdUser)
                .toList();

        // Una sola query para todos los perfiles de abogado
        Map<Long, Boolean> isVerifiedByUserId = lawyerUserIds.isEmpty()
                ? Collections.emptyMap()
                : lawyerProfileRepository.findByUserIdIn(lawyerUserIds)
                .stream()
                .collect(Collectors.toMap(
                        lp -> lp.getUser().getIdUser(),
                        LawyerProfile::getIsVerified
                ));

        return users.map(user -> mapToUserListResponse(user, isVerifiedByUserId));
    }

    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUserDetail(UUID publicId) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Optional<LawyerProfile> lawyerProfile = Optional.empty();
        Optional<IdentityDocument> kycDocument = Optional.empty();

        if (user.hasRole("LAWYER")) {
            lawyerProfile = lawyerProfileRepository.findByUserId(user.getIdUser());
        }

        kycDocument = identityDocumentRepository.findByUser(user);

        return mapToUserDetailResponse(user, lawyerProfile, kycDocument);
    }

    @Transactional
    public void updateAccountStatus(UUID publicId, UpdateAccountStatusRequest request) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        AccountStatus newStatus = AccountStatus.valueOf(request.accountStatus());
        user.setAccountStatus(newStatus);
        userRepository.save(user);
    }

    // ── LAWYER VERIFICATION ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AdminLawyerPendingResponse> getPendingLawyers() {
        List<LawyerProfile> pendingLawyers = lawyerProfileRepository
                .findByVerificationStatus(LawyerProfile.VerificationStatus.PENDING);

        return pendingLawyers.stream()
                .map(this::mapToPendingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void verifyLawyer(UUID userPublicId, VerifyLawyerRequest request) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        LawyerProfile profile = lawyerProfileRepository.findByUserId(user.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de abogado no encontrado"));

        LawyerProfile.VerificationStatus newStatus =
                LawyerProfile.VerificationStatus.valueOf(request.verificationStatus());

        profile.setVerificationStatus(newStatus);
        profile.setIsVerified(newStatus == LawyerProfile.VerificationStatus.VERIFIED);

        // Also verify the identity document if approving
        if (newStatus == LawyerProfile.VerificationStatus.VERIFIED) {
            identityDocumentRepository.findByUser(user).ifPresent(doc -> {
                doc.setIsVerified(true);
                doc.setVerifiedAt(OffsetDateTime.now());
                identityDocumentRepository.save(doc);
            });
        }

        lawyerProfileRepository.save(profile);
    }

    // ── SPECIALTIES MANAGEMENT ───────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getAllSpecialties() {
        return specialtyRepository.findAll().stream()
                .map(this::mapToSpecialtyResponse)
                .toList();
    }

    @Transactional
    public SpecialtyResponse createSpecialty(CreateSpecialtyRequest request) {
        Specialty specialty = new Specialty();
        specialty.setName(request.name());
        specialty.setDescription(request.description());
        specialty.setIsActive(request.isActive() != null ? request.isActive() : true);

        specialty = specialtyRepository.save(specialty);
        return mapToSpecialtyResponse(specialty);
    }

    @Transactional
    public SpecialtyResponse updateSpecialty(Long id, CreateSpecialtyRequest request) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        specialty.setName(request.name());
        specialty.setDescription(request.description());
        if (request.isActive() != null) {
            specialty.setIsActive(request.isActive());
        }

        specialty = specialtyRepository.save(specialty);
        return mapToSpecialtyResponse(specialty);
    }

    @Transactional
    public void deleteSpecialty(Long id) {
        if (!specialtyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Especialidad no encontrada");
        }
        specialtyRepository.deleteById(id);
    }

    @Transactional
    public SpecialtyResponse toggleSpecialtyStatus(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        specialty.setIsActive(!specialty.getIsActive());
        specialty = specialtyRepository.save(specialty);
        return mapToSpecialtyResponse(specialty);
    }

    // ── DOCUMENT TEMPLATES MANAGEMENT ────────────────────────────────

    @Transactional(readOnly = true)
    public List<DocumentTemplateResponse> getAllDocumentTemplates() {
        return documentTemplateRepository.findAll().stream()
                .map(this::mapToDocumentTemplateResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentTemplateResponse getDocumentTemplate(UUID publicId) {
        DocumentTemplate template = documentTemplateRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada"));
        return mapToDocumentTemplateResponse(template);
    }

    @Transactional
    public DocumentTemplateResponse createDocumentTemplate(DocumentTemplateRequest request) {
        DocumentTemplate template = new DocumentTemplate();
        updateTemplateFromRequest(template, request);
        template = documentTemplateRepository.save(template);
        return mapToDocumentTemplateResponse(template);
    }

    @Transactional
    public DocumentTemplateResponse updateDocumentTemplate(UUID publicId, DocumentTemplateRequest request) {
        DocumentTemplate template = documentTemplateRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada"));
        
        updateTemplateFromRequest(template, request);
        template = documentTemplateRepository.save(template);
        return mapToDocumentTemplateResponse(template);
    }

    @Transactional
    public void deleteDocumentTemplate(UUID publicId) {
        DocumentTemplate template = documentTemplateRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada"));
        documentTemplateRepository.delete(template);
    }

    private void updateTemplateFromRequest(DocumentTemplate template, DocumentTemplateRequest request) {
        template.setName(request.name());
        template.setCode(request.code());
        template.setJurisdiction(request.jurisdiction());
        template.setContent(request.content());
        template.setRequiredFields(request.requiredFields());
        template.setFieldDefinitions(request.fieldDefinitions());
        template.setIsActive(request.isActive());
    }

    // ── PRIVATE MAPPERS ───────────────────────────────────────────────

    private DocumentTemplateResponse mapToDocumentTemplateResponse(DocumentTemplate template) {
        return new DocumentTemplateResponse(
                template.getPublicId(),
                template.getName(),
                template.getCode(),
                template.getJurisdiction(),
                template.getContent(),
                template.getRequiredFields(),
                template.getFieldDefinitions(),
                template.getIsActive(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }

    private SpecialtyResponse mapToSpecialtyResponse(Specialty specialty) {
        return new SpecialtyResponse(
                specialty.getId(),
                specialty.getName(),
                specialty.getDescription(),
                specialty.getIsActive(),
                specialtyRepository.countLawyersBySpecialtyId(specialty.getId())
        );
    }

    private AdminUserListResponse mapToUserListResponse(User user, Map<Long, Boolean> isVerifiedByUserId) {
        boolean isVerified = isVerifiedByUserId.getOrDefault(user.getIdUser(), false);

        return new AdminUserListResponse(
                user.getPublicId(),
                user.getFullName(),
                user.getEmail(),
                user.getRoleName(),
                user.getAccountStatus().name(),
                isVerified,
                user.getCreatedAt()
        );
    }

    private AdminUserDetailResponse mapToUserDetailResponse(
            User user,
            Optional<LawyerProfile> lawyerProfile,
            Optional<IdentityDocument> kycDocument
    ) {
        return new AdminUserDetailResponse(
                user.getPublicId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoleName(),
                user.getAccountStatus().name(),
                user.getOnboardingStep().name(),
                user.getCreatedAt(),
                lawyerProfile.map(LawyerProfile::getCity).orElse(null),
                lawyerProfile.map(LawyerProfile::getCountry).orElse(null),
                lawyerProfile.map(LawyerProfile::getBarRegistrationNumber).orElse(null),
                lawyerProfile.map(LawyerProfile::getBarAssociation).orElse(null),
                lawyerProfile.map(lp -> lp.getVerificationStatus().name()).orElse(null),
                lawyerProfile.map(LawyerProfile::getIsVerified).orElse(false),
                lawyerProfile.map(LawyerProfile::getHourlyRate).orElse(null),
                lawyerProfile.map(LawyerProfile::getCurrency).orElse(null),
                lawyerProfile.map(LawyerProfile::getRatingAvg).orElse(null),
                lawyerProfile.map(LawyerProfile::getReviewCount).orElse(0),
                kycDocument.map(IdentityDocument::getDocumentType).orElse(null),
                kycDocument.map(IdentityDocument::getDocumentNumber).orElse(null),
                kycDocument.map(IdentityDocument::getCountryCode).orElse(null),
                kycDocument.map(IdentityDocument::getIsVerified).orElse(false)
        );
    }

    private AdminLawyerPendingResponse mapToPendingResponse(LawyerProfile profile) {
        User user = profile.getUser();
        Optional<IdentityDocument> kyc = identityDocumentRepository.findByUser(user);

        return new AdminLawyerPendingResponse(
                user.getPublicId(),
                profile.getPublicId(),
                user.getFullName(),
                user.getEmail(),
                profile.getCity(),
                profile.getCountry(),
                profile.getBarRegistrationNumber(),
                profile.getBarAssociation(),
                kyc.map(IdentityDocument::getDocumentType).orElse(null),
                kyc.map(IdentityDocument::getDocumentNumber).orElse(null),
                profile.getCreatedAt()
        );
    }
}
