package com.saas.legit.module.identity.dto;

import java.math.BigDecimal;
import java.util.List;

public record PublicProfileResponse(
    String fullName,
    String avatarUrl,
    String bio,
    String city,
    String country,
    BigDecimal hourlyRate,
    String currency,
    String barAssociation,
    String barRegistrationNumber,
    BigDecimal ratingAvg,
    Integer reviewCount,
    List<SpecialtyDTO> specialties,
    List<ScheduleDTO> schedules
) {
    public record SpecialtyDTO(String name, String description) {}
    public record ScheduleDTO(Integer dayOfWeek, String startTime, String endTime) {}
}
