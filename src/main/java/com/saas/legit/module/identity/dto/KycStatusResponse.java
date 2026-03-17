package com.saas.legit.module.identity.dto;

public record KycStatusResponse(
        boolean hasDocument,
        String verificationStatus,
        String documentType
) {}
