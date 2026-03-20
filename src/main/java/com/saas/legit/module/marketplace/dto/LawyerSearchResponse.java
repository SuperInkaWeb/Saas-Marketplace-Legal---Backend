package com.saas.legit.module.marketplace.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record LawyerSearchResponse(
        UUID publicId,
        String slug,
        String fullName,
        String avatarUrl,
        String city,
        String country,
        BigDecimal hourlyRate,
        String currency,
        BigDecimal ratingAvg,
        Integer reviewCount,
        Boolean isVerified,
        List<String> specialties
) {}
