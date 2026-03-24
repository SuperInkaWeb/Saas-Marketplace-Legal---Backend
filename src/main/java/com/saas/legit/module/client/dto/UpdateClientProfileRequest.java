package com.saas.legit.module.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClientProfileRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido paterno es obligatorio")
        String lastNameFather,

        @NotBlank(message = "El apellido materno es obligatorio")
        String lastNameMother,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 20)
        String phoneNumber,

        String companyName,
        String billingAddress
) {}
