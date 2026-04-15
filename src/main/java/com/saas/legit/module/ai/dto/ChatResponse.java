package com.saas.legit.module.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String sessionPublicId;
    private String responseMessage;
}
