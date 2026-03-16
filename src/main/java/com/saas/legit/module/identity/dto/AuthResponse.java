package com.saas.legit.module.identity.dto;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        UUID publicId,
        String email,
        String fullName,
        List<String> roles,
        String accessToken
) {}