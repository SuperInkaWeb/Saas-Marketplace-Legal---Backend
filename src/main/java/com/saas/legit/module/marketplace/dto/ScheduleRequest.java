package com.saas.legit.module.marketplace.dto;

import com.saas.legit.module.marketplace.validation.ValidTimeRange;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ValidTimeRange
public record ScheduleRequest(
        @NotNull(message = "El día de la semana es obligatorio")
        @Min(value = 1, message = "El día debe estar entre 1 (Lunes) y 7 (Domingo)")
        @Max(value = 7, message = "El día debe estar entre 1 (Lunes) y 7 (Domingo)")
        Integer dayOfWeek,

        @NotBlank(message = "La hora de inicio es obligatoria")
        String startTime,

        @NotBlank(message = "La hora de fin es obligatoria")
        String endTime
) {}
