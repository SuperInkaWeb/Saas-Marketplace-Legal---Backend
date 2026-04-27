package com.saas.legit.module.marketplace.dto;

import com.saas.legit.module.marketplace.model.CaseRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CaseWithProposalsResponse {
    private UUID publicId;
    private String title;
    private String description;
    private BigDecimal budget;
    private String currency;
    private String specialtyName;
    private String clientName;
    private String clientAvatarUrl;
    private CaseRequestStatus status;
    private OffsetDateTime createdAt;
    private List<LawyerProposalResponse> proposals;
}