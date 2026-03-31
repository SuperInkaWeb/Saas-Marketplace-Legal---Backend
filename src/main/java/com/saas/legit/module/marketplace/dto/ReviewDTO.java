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
    private String replyText;
    private OffsetDateTime repliedAt;
    private OffsetDateTime createdAt;
    private Boolean isFeatured;

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

    @Data
    public static class ReplyRequest {
        @NotNull(message = "El comentario de respuesta es obligatorio")
        @Size(min = 1, max = 2000, message = "La respuesta debe tener entre 1 y 2000 caracteres")
        private String replyText;
    }
}
