package com.saas.legit.module.identity.service;

import com.saas.legit.module.identity.dto.UserMeResponse;
import com.saas.legit.module.identity.model.IdentityDocument;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
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
public class UserService {

    private static final String ROLE_CLIENT = "CLIENT";
    private static final String ROLE_LAWYER = "LAWYER";

    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final IdentityDocumentRepository identityDocumentRepository;

    @Transactional(readOnly = true)
    public UserMeResponse getMe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        boolean hasProfile = resolveHasProfile(user);
        boolean isVerified = resolveIsVerified(user);

        return new UserMeResponse(
                user.getPublicId(),
                user.getEmail(),
                user.getFullName(),
                user.getRoleName(),
                user.getOnboardingStep().name(),
                user.getAccountStatus().name(),
                hasProfile,
                isVerified
        );
    }

    private boolean resolveHasProfile(User user) {
        if (user.hasRole(ROLE_CLIENT)) {
            return clientProfileRepository.existsByUser(user);
        }
        if (user.hasRole(ROLE_LAWYER)) {
            return lawyerProfileRepository.findByUserId(user.getIdUser()).isPresent();
        }
        return false;
    }

    private boolean resolveIsVerified(User user) {
        if (user.hasRole(ROLE_LAWYER)) {
            return lawyerProfileRepository.findByUserId(user.getIdUser())
                    .map(lp -> lp.getVerificationStatus() == LawyerProfile.VerificationStatus.VERIFIED)
                    .orElse(false);
        }
        if (user.hasRole(ROLE_CLIENT)) {
            // Clients are "verified" if they have a verified identity doc (optional)
            Optional<IdentityDocument> doc = identityDocumentRepository.findByUser(user);
            return doc.map(IdentityDocument::getIsVerified).orElse(false);
        }
        return false;
    }
}
