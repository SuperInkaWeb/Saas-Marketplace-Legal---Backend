package com.saas.legit.module.marketplace.dto;

import com.saas.legit.module.marketplace.model.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewReportRequest {
    @NotNull(message = "El motivo es requerido")
    private ReportReason reason;
    private String details;
}
