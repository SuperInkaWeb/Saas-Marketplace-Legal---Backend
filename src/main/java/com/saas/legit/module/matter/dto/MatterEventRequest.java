package com.saas.legit.module.matter.dto;

import com.saas.legit.module.matter.model.MatterEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class MatterEventRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    @NotNull
    private MatterEventType eventType;
    
    private OffsetDateTime eventDate;
    
    private UUID documentPublicId; // To link an existing document
}
