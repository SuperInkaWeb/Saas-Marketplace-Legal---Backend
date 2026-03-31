package com.saas.legit.module.marketplace.service;


import com.saas.legit.module.appointment.repository.AppointmentRepository;
import com.saas.legit.module.identity.dto.PublicProfileResponse;
import com.saas.legit.module.identity.exception.InvalidOnboardingStepException;
import com.saas.legit.module.identity.model.OnboardingStep;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.dto.*;
import com.saas.legit.module.marketplace.exception.LawyerProfileNotFoundException;
import com.saas.legit.module.marketplace.exception.ScheduleConflictException;
import com.saas.legit.module.marketplace.exception.ScheduleNotFoundException;
import com.saas.legit.module.marketplace.exception.SpecialtyNotFoundException;
import com.saas.legit.module.marketplace.exception.UnauthorizedAccessException;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.model.LawyerSchedule;
import com.saas.legit.module.marketplace.model.Review;
import com.saas.legit.module.marketplace.model.Specialty;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import com.saas.legit.module.marketplace.repository.LawyerProposalRepository;
import com.saas.legit.module.marketplace.repository.LawyerScheduleRepository;
import com.saas.legit.module.marketplace.repository.ReviewRepository;
import com.saas.legit.module.marketplace.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LawyerProfileConfigService {

    private static final String ROLE_LAWYER = "LAWYER";

    private final UserRepository userRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final LawyerScheduleRepository lawyerScheduleRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ReviewRepository reviewRepository;
    private final LawyerProposalRepository lawyerProposalRepository;
    private final AppointmentRepository appointmentRepository;

    // ── CREATE LAWYER PROFILE ──────────────────────────────────────────

    @Transactional
    public void createLawyerProfile(Long userId, CreateLawyerProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow();

        requireStep(user);
        requireRole(user);

        if (lawyerProfileRepository.findByUserId(user.getIdUser()).isPresent()) {
            user.setOnboardingStep(OnboardingStep.KYC_PENDING);
            userRepository.save(user);
            return;
        }

        String slug = generateUniqueSlug(user.getFirstName(), user.getLastNameFather());
        LawyerProfile profile = new LawyerProfile(user, slug, request.city(), request.country());
        lawyerProfileRepository.save(profile);

        user.setOnboardingStep(OnboardingStep.KYC_PENDING);
        userRepository.save(user);
    }

    // ── UPDATE LAWYER PROFILE ──────────────────────────────────────────

    @Transactional
    public void updateProfile(Long userId, UpdateLawyerProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        LawyerProfile profile = findProfileByUserId(userId);

        user.setFirstName(request.firstName());
        user.setLastNameFather(request.lastNameFather());
        user.setLastNameMother(request.lastNameMother());
        user.setPhoneNumber(request.phoneNumber());

        profile.setBioLawyer(request.bio());
        profile.setCity(request.city());
        profile.setCountry(request.country());
        profile.setLatitude(request.latitude());
        profile.setLongitude(request.longitude());
        profile.setHourlyRate(request.hourlyRate());
        profile.setCurrency(request.currency());
        profile.setBarRegistrationNumber(request.barRegistrationNumber());
        profile.setBarAssociation(request.barAssociation());

        userRepository.save(user);
        lawyerProfileRepository.save(profile);
    }

    // ── GET FULL CONFIG ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public LawyerProfileConfigResponse getMyProfileConfig(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        LawyerProfile profile = findProfileByUserId(userId);

        List<LawyerProfileConfigResponse.SpecialtyInfo> specialtyInfos = profile.getSpecialties().stream()
                .map(s -> new LawyerProfileConfigResponse.SpecialtyInfo(s.getId(), s.getName(), s.getDescription()))
                .toList();

        List<LawyerProfileConfigResponse.ScheduleInfo> scheduleInfos = profile.getSchedules().stream()
                .map(s -> new LawyerProfileConfigResponse.ScheduleInfo(
                        s.getId(),
                        s.getDayOfWeek(),
                        s.getStartTime().toString(),
                        s.getEndTime().toString(),
                        s.getIsActive()))
                .toList();

        return new LawyerProfileConfigResponse(
                user.getFirstName(),
                user.getLastNameFather(),
                user.getLastNameMother(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getAvatarURL(),
                profile.getSlugLawyerProfile(),
                profile.getBioLawyer(),
                profile.getCity(),
                profile.getCountry(),
                profile.getLatitude(),
                profile.getLongitude(),
                profile.getHourlyRate(),
                profile.getCurrency(),
                profile.getBarRegistrationNumber(),
                profile.getBarAssociation(),
                profile.getVerificationStatus().name(),
                profile.getIsVerified(),
                profile.getRatingAvg(),
                profile.getReviewCount(),

                specialtyInfos,
                scheduleInfos
        );
    }

    // ── PUBLIC PROFILE (by slug) ──────────────────────────────────────

    @Transactional(readOnly = true)
    public PublicProfileResponse getPublicProfile(String slug) {
        LawyerProfile profile = lawyerProfileRepository.findBySlug(slug)
                .orElseThrow(LawyerProfileNotFoundException::new);

        User user = profile.getUser();

        List<PublicProfileResponse.SpecialtyDTO> specialtyDTOs = profile.getSpecialties().stream()
                .map(s -> new PublicProfileResponse.SpecialtyDTO(s.getName(), s.getDescription()))
                .toList();

        List<PublicProfileResponse.ScheduleDTO> scheduleDTOs = profile.getSchedules().stream()
                .map(s -> new PublicProfileResponse.ScheduleDTO(
                        s.getDayOfWeek(),
                        s.getStartTime().toString(),
                        s.getEndTime().toString()))
                .toList();

        return new PublicProfileResponse(
                user.getPublicId(),
                user.getFirstName() + " " + user.getLastNameFather() + " " + user.getLastNameMother(),
                user.getAvatarURL(),
                profile.getBioLawyer(),
                profile.getCity(),
                profile.getCountry(),
                profile.getHourlyRate(),
                profile.getCurrency(),
                profile.getBarAssociation(),
                profile.getBarRegistrationNumber(),
                profile.getRatingAvg(),
                profile.getReviewCount(),
                getRatingBreakdown(profile.getIdLawyerProfile()),
                specialtyDTOs,
                scheduleDTOs
        );
    }

    // ── SEARCH LAWYERS (marketplace) ──────────────────────────────────

    @Transactional(readOnly = true)
    public Page<LawyerSearchResponse> searchLawyers(String query, Long specialtyId, BigDecimal minRating, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LawyerProfile> profiles = lawyerProfileRepository.searchVerifiedLawyers(query, specialtyId, minRating, pageable);
        return profiles.map(this::toSearchResponse);
    }

    // ── SPECIALTIES ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getAllSpecialties() {
        return specialtyRepository.findAll().stream()
                .map(s -> new SpecialtyResponse(
                        s.getId(),
                        s.getName(),
                        s.getDescription(),
                        s.getIsActive(),
                        specialtyRepository.countLawyersBySpecialtyId(s.getId())
                ))
                .toList();
    }

    @Transactional
    public void updateSpecialties(Long userId, UpdateSpecialtiesRequest request) {
        LawyerProfile profile = findProfileByUserId(userId);

        List<Specialty> specialties = specialtyRepository.findAllByIdIn(request.specialtyIds());

        if (specialties.size() != request.specialtyIds().size()) {
            List<Long> foundIds = specialties.stream().map(Specialty::getId).toList();
            List<Long> missingIds = request.specialtyIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new SpecialtyNotFoundException(missingIds);
        }

        profile.setSpecialties(new HashSet<>(specialties));
        lawyerProfileRepository.save(profile);
    }

    // ── SCHEDULES ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getMySchedules(Long userId) {
        LawyerProfile profile = findProfileByUserId(userId);

        return lawyerScheduleRepository
                .findByLawyerProfileOrderByDayOfWeekAscStartTimeAsc(profile)
                .stream()
                .map(this::toScheduleResponse)
                .toList();
    }

    @Transactional
    public ScheduleResponse addSchedule(Long userId, ScheduleRequest request) {
        LawyerProfile profile = findProfileByUserId(userId);

        LawyerSchedule schedule = new LawyerSchedule();
        schedule.setLawyerProfile(profile);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(LocalTime.parse(request.startTime()));
        schedule.setEndTime(LocalTime.parse(request.endTime()));
        schedule.setIsActive(true);

        try {
            schedule = lawyerScheduleRepository.save(schedule);
        } catch (DataIntegrityViolationException ex) {
            throw new ScheduleConflictException();
        }

        return toScheduleResponse(schedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleRequest request) {
        LawyerProfile profile = findProfileByUserId(userId);

        LawyerSchedule schedule = lawyerScheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        if (!schedule.getLawyerProfile().getIdLawyerProfile().equals(profile.getIdLawyerProfile())) {
            throw new UnauthorizedAccessException("horario");
        }

        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(LocalTime.parse(request.startTime()));
        schedule.setEndTime(LocalTime.parse(request.endTime()));

        try {
            schedule = lawyerScheduleRepository.save(schedule);
        } catch (DataIntegrityViolationException ex) {
            throw new ScheduleConflictException();
        }

        return toScheduleResponse(schedule);
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        LawyerProfile profile = findProfileByUserId(userId);

        LawyerSchedule schedule = lawyerScheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        if (!schedule.getLawyerProfile().getIdLawyerProfile().equals(profile.getIdLawyerProfile())) {
            throw new UnauthorizedAccessException("horario");
        }

        lawyerScheduleRepository.delete(schedule);
    }

    // ── REVIEWS ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(Long userId) {
        LawyerProfile profile = findProfileByUserId(userId);

        return reviewRepository.findByLawyerIdOrderByFeaturedFirst(profile.getIdLawyerProfile())
                .stream()
                .map(this::toReviewResponse)
                .toList();
    }

    // ── PROPOSALS ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<LawyerProposalResponse> getMyProposals(Long userId) {
        LawyerProfile profile = findProfileByUserId(userId);

        return lawyerProposalRepository
                .findByLawyerProfile_IdLawyerProfileOrderByCreatedAtDesc(profile.getIdLawyerProfile())
                .stream()
                .map(proposal -> LawyerProposalResponse.builder()
                        .id(proposal.getId())
                        .lawyerName(profile.getUser().getFirstName() + " " + profile.getUser().getLastNameFather())
                        .lawyerPublicId(profile.getPublicId().toString())
                        .proposalText(proposal.getProposalText())
                        .proposedFee(proposal.getProposedFee())
                        .status(proposal.getStatus())
                        .createdAt(proposal.getCreatedAt())
                        .build())
                .toList();
    }

    // ── DASHBOARD STATS ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(Long userId) {
        LawyerProfile profile = findProfileByUserId(userId);
        Long profileId = profile.getIdLawyerProfile();

        int pendingAppointments = appointmentRepository.countPendingByLawyer(profileId);
        int totalProposals = lawyerProposalRepository
                .findByLawyerProfile_IdLawyerProfileOrderByCreatedAtDesc(profileId).size();

        return new DashboardStatsResponse(
                pendingAppointments,
                totalProposals,
                profile.getRatingAvg(),
                profile.getReviewCount(),
                getRatingBreakdown(profileId)
        );
    }


    // ── PRIVATE HELPERS ────────────────────────────────────────────────

    private Map<Integer, Long> getRatingBreakdown(Long lawyerId) {
        List<Object[]> counts = reviewRepository.countRatingsByLawyerId(lawyerId);
        Map<Integer, Long> breakdown = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            breakdown.put(i, 0L);
        }
        for (Object[] row : counts) {
            if (row[0] != null) {
                Integer rating = ((Short) row[0]).intValue();
                Long count = (Long) row[1];
                breakdown.put(rating, count);
            }
        }
        return breakdown;
    }

    private LawyerProfile findProfileByUserId(Long userId) {
        return lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(LawyerProfileNotFoundException::new);
    }

    private void requireStep(User user) {
        if (user.getOnboardingStep() != OnboardingStep.PROFILE_PENDING) {
            throw new InvalidOnboardingStepException(
                    user.getOnboardingStep().name(), OnboardingStep.PROFILE_PENDING.name()
            );
        }
    }

    private void requireRole(User user) {
        if (!user.hasRole(ROLE_LAWYER)) {
            throw new InvalidOnboardingStepException(
                    "ROL_INCORRECTO",
                    "El rol del usuario debe ser " + ROLE_LAWYER
            );
        }
    }

    private String generateUniqueSlug(String firstName, String lastName) {
        String base = Normalizer.normalize(firstName + "-" + lastName, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        String slug = base;
        while (lawyerProfileRepository.existsBySlugLawyerProfile(slug)) {
            slug = base + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        return slug;
    }

    private ScheduleResponse toScheduleResponse(LawyerSchedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime().toString(),
                schedule.getEndTime().toString(),
                schedule.getIsActive()
        );
    }

    private ReviewResponse toReviewResponse(Review review) {
        String clientName;
        if (Boolean.TRUE.equals(review.getIsAnonymous())) {
            clientName = "Usuario Anónimo";
        } else {
            clientName = review.getClientProfile().getUser().getFirstName()
                    + " " + review.getClientProfile().getUser().getLastNameFather().charAt(0) + ".";
        }
        
        return new ReviewResponse(
                review.getPublicId(),
                clientName,
                review.getRating(),
                review.getComment(),
                review.getReplyText(),
                review.getRepliedAt(),
                review.getCreatedAt(),
                Boolean.TRUE.equals(review.getIsFeatured())
        );
    }

    private LawyerSearchResponse toSearchResponse(LawyerProfile profile) {
        User user = profile.getUser();
        List<String> specialtyNames = profile.getSpecialties().stream()
                .map(Specialty::getName)
                .toList();

        return new LawyerSearchResponse(
                profile.getPublicId(),
                profile.getSlugLawyerProfile(),
                user.getFirstName() + " " + user.getLastNameFather() + " " + user.getLastNameMother(),
                user.getAvatarURL(),
                profile.getCity(),
                profile.getCountry(),
                profile.getHourlyRate(),
                profile.getCurrency(),
                profile.getRatingAvg(),
                profile.getReviewCount(),
                profile.getIsVerified(),
                specialtyNames
        );
    }
}
