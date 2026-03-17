package com.saas.legit.module.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record UploadIdentityDocumentRequest(
        @NotBlank(message = "El tipo de documento es obligatorio")
        String documentType,

        @NotBlank(message = "El número de documento es obligatorio")
        String documentNumber,

        @NotBlank(message = "El código de país es obligatorio")
        String documentCountryCode
) {}
