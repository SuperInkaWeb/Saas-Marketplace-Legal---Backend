package com.saas.legit.module.admin.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DocumentTemplateResponse(
        UUID publicId,
        String name,
        String code,
        String jurisdiction,
        String content,
        String requiredFields,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
