package com.saas.legit.module.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyLawyerRequest(
        @NotBlank(message = "El estado de verificación es requerido")
        @Pattern(regexp = "VERIFIED|REJECTED", message = "Estado inválido. Valores permitidos: VERIFIED, REJECTED")
        String verificationStatus,

        String rejectionReason
) {}
