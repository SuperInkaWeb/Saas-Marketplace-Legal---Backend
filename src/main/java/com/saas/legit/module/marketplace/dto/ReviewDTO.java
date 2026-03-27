package com.saas.legit.module.marketplace.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class ReviewDTO {
    private UUID publicId;
    private UUID lawyerPublicId;
    private UUID appointmentPublicId;
    private String clientName;
    private Short rating;
    private String comment;
    private Boolean isAnonymous;
    private OffsetDateTime createdAt;

    @Data
    public static class Create {

        @NotNull(message = "El ID de la cita es obligatorio")
        private UUID appointmentPublicId;

        @NotNull(message = "La calificación es obligatoria")
        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        private Short rating;

        @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
        private String comment;

        private Boolean isAnonymous = false;
    }
}
