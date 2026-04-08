package com.saas.legit.module.document.dto;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DocumentGeneratorResponse {
    private String generatedContent; // The markdown/HTML text
    private List<String> missingFields; // Fields that the user didn't provide and are required
    
    @JsonProperty("isValid")
    private boolean isValid; // True if all required fields are provided
    
    private UUID documentPublicId; // If we saved the document draft to DB
}
