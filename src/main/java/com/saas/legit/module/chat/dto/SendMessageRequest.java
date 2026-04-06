package com.saas.legit.module.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 5000, message = "El mensaje no puede exceder los 5000 caracteres")
    String text
) {}
