package com.saas.legit.module.marketplace.dto;

public record SpecialtyResponse(
        Long id,
        String name,
        String description,
        Boolean isActive,
        Long lawyerCount
) {}
