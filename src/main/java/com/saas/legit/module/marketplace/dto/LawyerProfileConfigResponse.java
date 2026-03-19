package com.saas.legit.module.marketplace.dto;

import java.math.BigDecimal;
import java.util.List;

public record LawyerProfileConfigResponse(
        // User info
        String firstName,
        String lastNameFather,
        String lastNameMother,
        String phoneNumber,
        String email,
        String avatarUrl,

        // Lawyer profile info
        String slug,
        String bio,
        String city,
        String country,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal hourlyRate,
        String currency,
        String barRegistrationNumber,
        String barAssociation,
        String verificationStatus,
        Boolean isVerified,
        BigDecimal ratingAvg,
        Integer reviewCount,

        // Specialties
        List<SpecialtyInfo> specialties,

        // Schedules
        List<ScheduleInfo> schedules
) {
    public record SpecialtyInfo(Long id, String name, String description) {}
    public record ScheduleInfo(Long id, Integer dayOfWeek, String startTime, String endTime, Boolean isActive) {}
}
