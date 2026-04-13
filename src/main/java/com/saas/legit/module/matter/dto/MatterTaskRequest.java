package com.saas.legit.module.matter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MatterTaskRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    private OffsetDateTime dueDate;
}
