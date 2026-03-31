package com.saas.legit.module.marketplace.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.marketplace.dto.ReviewReportDTO;
import com.saas.legit.module.marketplace.dto.ReviewReportRequest;
import com.saas.legit.module.marketplace.model.ReviewReport;
import com.saas.legit.module.marketplace.service.ReviewReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewReportController {

    private final ReviewReportService reportService;

    @PostMapping("/{publicId}/report")
    public ResponseEntity<Void> reportReview(
            @PathVariable UUID publicId,
            @Valid @RequestBody ReviewReportRequest request) {
        Long reporterUserId = SecurityUtils.getCurrentUser().userId();
        reportService.reportReview(publicId, reporterUserId, request.getReason(), request.getDetails());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/reports")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReviewReportDTO>> getPendingReports() {
        return ResponseEntity.ok(reportService.getPendingReports().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList()));
    }

    @PatchMapping("/admin/reports/{publicId}/resolve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> resolveReport(
            @PathVariable UUID publicId,
            @RequestParam boolean deleteReview) {
        reportService.resolveReport(publicId, deleteReview);
        return ResponseEntity.ok().build();
    }

    private ReviewReportDTO mapToDTO(ReviewReport report) {
        return ReviewReportDTO.builder()
                .publicId(report.getPublicId())
                .reviewId(report.getReview().getPublicId())
                .reviewComment(report.getReview().getComment())
                .clientName(report.getReview().getClientProfile().getUser().getFullName())
                .reporterId(report.getReporter().getPublicId())
                .reporterName(report.getReporter().getFullName())
                .reason(report.getReason())
                .details(report.getDetails())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
