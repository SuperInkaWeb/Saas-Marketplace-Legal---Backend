package com.saas.legit.module.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @Email(message = "Formato de email inválido")
        @NotBlank(message = "El email es obligatorio")
        String email
) {}
