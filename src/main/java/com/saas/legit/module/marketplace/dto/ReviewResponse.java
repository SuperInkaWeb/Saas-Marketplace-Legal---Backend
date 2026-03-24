package com.saas.legit.module.marketplace.dto;

import java.time.OffsetDateTime;

public record ReviewResponse(
        String clientName,
        Short rating,
        String comment,
        OffsetDateTime createdAt
) {}
