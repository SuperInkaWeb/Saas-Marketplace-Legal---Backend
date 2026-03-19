package com.saas.legit.module.document.dto;

import com.saas.legit.module.document.model.SignatureStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class DocumentResponse {
    private UUID publicId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;
    private Boolean isTemplate;
    private BigDecimal price;
    private SignatureStatus signatureStatus;
    private Boolean isArchived;
    private OffsetDateTime createdAt;
}
