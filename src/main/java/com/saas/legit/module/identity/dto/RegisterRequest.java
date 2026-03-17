package com.saas.legit.module.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
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

        @NotBlank(message = "El número de teléfono es obligatorio")
        @Pattern(regexp = "^\\+?[0-9\\s-]{7,15}$", message = "Formato de teléfono inválido")
        String phoneNumber
) {}
