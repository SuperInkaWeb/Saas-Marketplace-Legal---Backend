package com.saas.legit.module.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record SelectRoleRequest(
        @NotBlank(message = "El rol es obligatorio")
        String role
) {}
