package com.saas.legit.module.matter.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class MatterCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Jurisdiction is required")
    private String jurisdiction;
    
    private UUID clientPublicId;
    
    private String unregisteredClientName;
}
