package com.saas.legit.module.marketplace.dto;

import com.saas.legit.module.marketplace.model.CaseRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class CaseRequestResponse {
    private UUID publicId;
    private String clientName;
    private String title;
    private String description;
    private BigDecimal budget;
    private String specialtyName;
    private CaseRequestStatus status;
    private OffsetDateTime createdAt;
}
