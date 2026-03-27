package com.saas.legit.module.identity.service;

import com.saas.legit.module.identity.dto.KycStatusResponse;
import com.saas.legit.module.identity.dto.UploadIdentityDocumentRequest;
import com.saas.legit.module.identity.exception.UnauthorizedKycAccessException;
import com.saas.legit.module.identity.model.IdentityDocument;
import com.saas.legit.module.identity.model.OnboardingStep;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.IdentityDocumentRepository;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KycService {

    private static final String ROLE_LAWYER = "LAWYER";

    private final UserRepository userRepository;
    private final IdentityDocumentRepository identityDocumentRepository;
    private final LawyerProfileRepository lawyerProfileRepository;

    // ── UPLOAD DOCUMENT ────────────────────────────────────────────────

    @Transactional
    public void uploadDocument(Long userId, UploadIdentityDocumentRequest request) {
        User user = userRepository.findById(userId).orElseThrow();

        // Only LAWYER can upload during onboarding; CLIENT uploads on-demand via domain services
        if (!user.hasRole(ROLE_LAWYER)) {
            throw new UnauthorizedKycAccessException();
        }

        Optional<IdentityDocument> existing = identityDocumentRepository.findByUser(user);

        if (existing.isPresent()) {
            IdentityDocument doc = existing.get();
            // Allow retry if REJECTED, otherwise block duplicate
            if (doc.getIsVerified() || isPending(doc)) {
                return; // idempotent: already has a pending/verified doc
            }
            // REJECTED → update existing document
            doc.setDocumentType(request.documentType());
            doc.setDocumentNumber(request.documentNumber());
            doc.setCountryCode(request.documentCountryCode());
            doc.setIsVerified(false);
            doc.setVerifiedAt(null);
            identityDocumentRepository.save(doc);
            user.setOnboardingStep(OnboardingStep.COMPLETED);
            userRepository.save(user);
            return;
        }

        IdentityDocument doc = new IdentityDocument(
                user,
                request.documentType(),
                request.documentNumber(),
                request.documentCountryCode()
        );
        identityDocumentRepository.save(doc);

        user.setOnboardingStep(OnboardingStep.COMPLETED);
        userRepository.save(user);
    }

    // ── KYC STATUS ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public KycStatusResponse getKycStatus(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        Optional<IdentityDocument> doc = identityDocumentRepository.findByUser(user);

        if (doc.isEmpty()) {
            return new KycStatusResponse(false, null, null, null, null);
        }

        IdentityDocument document = doc.get();
        String status = resolveVerificationStatus(user, document);

        return new KycStatusResponse(
                true,
                status,
                document.getDocumentType(),
                document.getDocumentNumber(),
                document.getCreatedAt()
        );
    }

    // ── PRIVATE HELPERS ────────────────────────────────────────────────

    private boolean isPending(IdentityDocument doc) {
        // A document is pending if it exists but is not verified and not rejected
        return !doc.getIsVerified() && doc.getVerifiedAt() == null;
    }

    private String resolveVerificationStatus(User user, IdentityDocument doc) {
        if (user.hasRole(ROLE_LAWYER)) {
            // For lawyers, check LawyerProfile.verificationStatus
            return lawyerProfileRepository.findByUserId(user.getIdUser())
                    .map(lp -> lp.getVerificationStatus().name())
                    .orElse("PENDING");
        }
        // For clients, simple doc status
        if (doc.getIsVerified()) return "VERIFIED";
        return "PENDING";
    }
}
