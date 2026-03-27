package com.saas.legit.module.identity.dto;

import java.time.OffsetDateTime;

public record KycStatusResponse(
        boolean hasDocument,
        String verificationStatus,
        String documentType,
        String documentNumber,
        OffsetDateTime submittedAt
) {}
