package com.saas.legit.module.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProposalRequest {
    @NotBlank(message = "Proposal text is required")
    private String proposalText;

    @NotNull(message = "Proposed fee is required")
    private BigDecimal proposedFee;
}
