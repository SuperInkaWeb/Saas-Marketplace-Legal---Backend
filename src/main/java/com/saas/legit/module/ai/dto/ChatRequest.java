package com.saas.legit.module.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "Message content is required")
    private String message;
    
    // Optional, if continuing an existing session
    private String sessionPublicId; 
}
