package com.saas.legit.module.identity.dto;

import java.util.UUID;

public record UserMeResponse(
        UUID publicId,
        String email,
        String fullName,
        String role,
        String onboardingStep,
        String accountStatus,
        boolean hasProfile,
        boolean isVerified
) {}
