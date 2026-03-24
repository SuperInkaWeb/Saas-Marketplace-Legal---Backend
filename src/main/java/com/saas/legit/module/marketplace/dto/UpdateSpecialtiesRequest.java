package com.saas.legit.module.marketplace.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdateSpecialtiesRequest(
        @NotEmpty(message = "Debe seleccionar al menos una especialidad")
        List<Long> specialtyIds
) {}
