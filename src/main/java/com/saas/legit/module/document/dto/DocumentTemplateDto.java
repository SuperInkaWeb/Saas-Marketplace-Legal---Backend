package com.saas.legit.module.document.dto;

import java.util.UUID;

public record DocumentTemplateDto(
        UUID publicId,
        String name,
        String code,
        String jurisdiction,
        String requiredFields,
        String fieldDefinitions
) {}
