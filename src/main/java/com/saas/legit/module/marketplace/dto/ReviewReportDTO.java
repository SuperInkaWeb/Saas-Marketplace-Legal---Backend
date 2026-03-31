package com.saas.legit.module.marketplace.dto;

import com.saas.legit.module.marketplace.model.ReportReason;
import com.saas.legit.module.marketplace.model.ReviewReport;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ReviewReportDTO {
    private UUID publicId;
    private UUID reviewId;
    private String reviewComment;
    private String clientName;
    private UUID reporterId;
    private String reporterName;
    private ReportReason reason;
    private String details;
    private ReviewReport.ReportStatus status;
    private OffsetDateTime createdAt;
}
