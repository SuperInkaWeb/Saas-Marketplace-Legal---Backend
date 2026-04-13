package com.saas.legit.module.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UploadDocumentRequest {
    @NotBlank(message = "File name is required")
    private String fileName;
    
    @NotBlank(message = "File URL is required")
    private String fileUrl;
    
    private String fileType;
    private Long fileSizeBytes;
    private Boolean isTemplate;
    private BigDecimal price;
    private UUID matterPublicId;
}
