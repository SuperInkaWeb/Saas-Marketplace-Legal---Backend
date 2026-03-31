package com.saas.legit.module.marketplace.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewResponse(
        UUID publicId,
        String clientName,
        Short rating,
        String comment,
        String replyText,
        OffsetDateTime repliedAt,
        OffsetDateTime createdAt,
        Boolean isFeatured
) {}
