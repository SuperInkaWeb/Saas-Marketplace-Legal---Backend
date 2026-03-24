package com.saas.legit.module.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateAccountStatusRequest(
        @NotBlank(message = "El estado es requerido")
        @Pattern(regexp = "ACTIVE|BLOCKED", message = "Estado inválido. Valores permitidos: ACTIVE, BLOCKED")
        String accountStatus
) {}
