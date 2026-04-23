package com.saas.legit.module.marketplace.dto;

import com.saas.legit.module.marketplace.model.ProposalStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class LawyerProposalResponse {
    private Long id;
    private String lawyerName;
    private String lawyerPublicId;
    private String lawyerSlug;
    private String proposalText;
    private BigDecimal proposedFee;
    private String currency;
    private ProposalStatus status;
    private String lawyerAvatarUrl;
    private OffsetDateTime createdAt;
}
