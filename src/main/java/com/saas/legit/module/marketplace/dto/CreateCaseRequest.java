package com.saas.legit.module.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateCaseRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede superar 255 caracteres")
        String title,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 20, max = 3000, message = "La descripción debe tener entre 20 y 3000 caracteres")
        String description,

        Long specialtyId,

        BigDecimal budget,
        String currency
) {}