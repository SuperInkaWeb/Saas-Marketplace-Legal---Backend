package com.saas.legit.module.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateLawyerProfileRequest(
                @NotBlank(message = "El nombre es obligatorio") String firstName,

                @NotBlank(message = "El apellido paterno es obligatorio") String lastNameFather,

                @NotBlank(message = "El apellido materno es obligatorio") String lastNameMother,

                @NotBlank(message = "El teléfono es obligatorio") @Size(max = 20) String phoneNumber,

                String bio,

                @NotBlank(message = "La ciudad es obligatoria") String city,

                @NotBlank(message = "El país es obligatorio") String country,

                BigDecimal latitude,

                BigDecimal longitude,

                @NotNull(message = "La tarifa por hora es obligatoria") BigDecimal hourlyRate,

                @NotBlank(message = "La moneda es obligatoria") @Size(max = 3) String currency,

                String barRegistrationNumber,
                String barAssociation) {
}

