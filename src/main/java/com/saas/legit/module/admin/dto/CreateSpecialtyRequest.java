package com.saas.legit.module.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSpecialtyRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
        String name,

        @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
        String description,

        Boolean isActive
) {}
