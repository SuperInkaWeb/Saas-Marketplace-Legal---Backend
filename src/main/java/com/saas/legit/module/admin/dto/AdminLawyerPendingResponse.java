package com.saas.legit.module.admin.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminLawyerPendingResponse(
        UUID userPublicId,
        UUID lawyerPublicId,
        String fullName,
        String email,
        String city,
        String country,
        String barRegistrationNumber,
        String barAssociation,
        String kycDocumentType,
        String kycDocumentNumber,
        OffsetDateTime createdAt
) {}
