package com.saas.legit.module.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentUpdateRequest {
    @NotBlank(message = "Document content is required")
    private String content; // Updated Markdown or HTML
}
