package com.saas.legit.module.marketplace.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLawyerProfileRequest(
        @NotBlank(message = "La ciudad es obligatoria")
        String city,

        @NotBlank(message = "El país es obligatorio")
        String country
) {}
