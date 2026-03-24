package com.saas.legit.module.admin.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminUserListResponse(
        UUID publicId,
        String fullName,
        String email,
        String role,
        String accountStatus,
        boolean isVerified,
        OffsetDateTime createdAt
) {}
