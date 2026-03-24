package com.saas.legit.module.admin.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminUserDetailResponse(
        UUID publicId,
        String fullName,
        String email,
        String phoneNumber,
        String role,
        String accountStatus,
        String onboardingStep,
        OffsetDateTime createdAt,

        // Lawyer-specific fields (null for clients)
        String city,
        String country,
        String barRegistrationNumber,
        String barAssociation,
        String verificationStatus,
        boolean isVerified,
        BigDecimal hourlyRate,
        String currency,
        BigDecimal ratingAvg,
        int reviewCount,

        // KYC fields
        String kycDocumentType,
        String kycDocumentNumber,
        String kycCountryCode,
        boolean kycIsVerified
) {}
