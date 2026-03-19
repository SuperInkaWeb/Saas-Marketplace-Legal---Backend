package com.saas.legit.module.identity.dto;

import java.math.BigDecimal;
import java.util.List;
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

                // Specialties (for lawyers)
                List<SpecialtyInfo> specialties,

                // Schedules (for lawyers)
                List<ScheduleInfo> schedules) {

    public record SpecialtyInfo(Long id, String name) {}
    public record ScheduleInfo(Long id, Integer dayOfWeek, String startTime, String endTime) {}
}

