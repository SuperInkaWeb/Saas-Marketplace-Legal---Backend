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

        String role = user.getRoleName();
        boolean hasProfile = false;
        String companyName = null;
        String billingAddress = null;
        String companyLogoUrl = null;
        String bio = null;
        String city = null;
        String country = null;
        java.math.BigDecimal hourlyRate = null;
        String currency = null;
        String barRegistrationNumber = null;
        String barAssociation = null;
        String lawFirmLogoUrl = null;
        String lawFirmCoverUrl = null;
        String slug = null;

        if (ROLE_CLIENT.equals(role)) {
            var profileOpt = clientProfileRepository.findByUser(user);
            if (profileOpt.isPresent()) {
                hasProfile = true;
                companyName = profileOpt.get().getCompanyName();
                billingAddress = profileOpt.get().getBillingAddress();
                companyLogoUrl = profileOpt.get().getCompanyURL();
            }
        } else if (ROLE_LAWYER.equals(role)) {
            var profileOpt = lawyerProfileRepository.findByUserId(user.getIdUser());
            if (profileOpt.isPresent()) {
                hasProfile = true;
                bio = profileOpt.get().getBioLawyer();
                city = profileOpt.get().getCity();
                country = profileOpt.get().getCountry();
                hourlyRate = profileOpt.get().getHourlyRate();
                currency = profileOpt.get().getCurrency();
                barRegistrationNumber = profileOpt.get().getBarRegistrationNumber();
                barAssociation = profileOpt.get().getBarAssociation();
                slug = profileOpt.get().getSlugLawyerProfile();
                
                var lawFirm = profileOpt.get().getLawFirm();
                if (lawFirm != null) {
                    lawFirmLogoUrl = lawFirm.getLogoUrl();
                    lawFirmCoverUrl = lawFirm.getCoverPhotoUrl();
                }
            }
        }

        boolean isVerified = resolveIsVerified(user);

        return new UserMeResponse(
                user.getPublicId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastNameFather(),
                user.getLastNameMother(),
                user.getPhoneNumber(),
                slug,
                user.getFullName(),
                role,
                user.getOnboardingStep().name(),
                user.getAccountStatus().name(),
                hasProfile,
                isVerified,
                user.getAvatarURL(),
                companyName,
                billingAddress,
                companyLogoUrl,
                bio,
                city,
                country,
                hourlyRate,
                currency,
                barRegistrationNumber,
                barAssociation,
                lawFirmLogoUrl,
                lawFirmCoverUrl
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
