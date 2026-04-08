package com.saas.legit.module.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentTemplateRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "El código es obligatorio") String code,
        @NotBlank(message = "La jurisdicción es obligatoria") String jurisdiction,
        @NotBlank(message = "El contenido es obligatorio") String content,
        String requiredFields,
        @NotNull(message = "El estado de la plantilla es obligatorio") Boolean isActive
) {}
