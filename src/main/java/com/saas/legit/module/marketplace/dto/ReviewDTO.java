package com.saas.legit.module.marketplace.dto;

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
        private UUID appointmentPublicId;
        private Short rating;
        private String comment;
        private Boolean isAnonymous = false;
    }
}
