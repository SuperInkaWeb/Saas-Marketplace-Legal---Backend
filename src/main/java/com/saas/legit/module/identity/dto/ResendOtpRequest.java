package com.saas.legit.module.identity.dto;

import com.saas.legit.module.notification.model.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResendOtpRequest(
        @Email(message = "Formato de email inválido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotNull(message = "El propósito del OTP es obligatorio")
        OtpPurpose purpose
) {}
