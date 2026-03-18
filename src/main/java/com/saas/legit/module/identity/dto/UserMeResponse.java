package com.saas.legit.module.identity.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UserMeResponse(
                UUID publicId,
                String email,
                String firstName,
                String lastNameFather,
                String lastNameMother,
                String phoneNumber,
                String slug,
                String fullName,
                String role,
                String onboardingStep,
                String accountStatus,
                boolean hasProfile,
                boolean isVerified,
                String avatarUrl,

                // Profile fields
                String companyName,
                String billingAddress,
                String companyLogoUrl,
                String bio,
                String city,
                String country,
                BigDecimal hourlyRate,
                String currency,
                String barRegistrationNumber,
                String barAssociation,

                // Law Firm fields (for lawyers)
                String lawFirmLogoUrl,
                String lawFirmCoverUrl) {
}
