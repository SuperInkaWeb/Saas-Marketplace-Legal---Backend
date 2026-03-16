package com.saas.legit.module.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LawyerRegistrationRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido paterno es obligatorio")
        String lastNameFather,

        @NotBlank(message = "El apellido materno es obligatorio")
        String lastNameMother,

        @Email(message = "Formato de email inválido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        String phoneNumber,

        @NotBlank(message = "La ciudad es obligatoria")
        String city,

        @NotBlank(message = "El país es obligatorio")
        String country,

        @NotBlank(message = "El tipo de documento es obligatorio")
        String documentType,

        @NotBlank(message = "El número de documento es obligatorio")
        String documentNumber,

        @NotBlank(message = "El código de país del documento es obligatorio")
        String documentCountryCode
) {}
