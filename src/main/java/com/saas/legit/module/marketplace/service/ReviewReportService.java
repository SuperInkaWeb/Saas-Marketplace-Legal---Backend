package com.saas.legit.module.marketplace.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.model.ReportReason;
import com.saas.legit.module.marketplace.model.Review;
import com.saas.legit.module.marketplace.model.ReviewReport;
import com.saas.legit.module.marketplace.repository.ReviewReportRepository;
import com.saas.legit.module.marketplace.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewReportService {

    private final ReviewReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewService reviewService;

    @Transactional
    public void reportReview(UUID reviewPublicId, Long reporterId, ReportReason reason, String details) {
        Review review = reviewRepository.findByPublicId(reviewPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración no encontrada"));
        
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        ReviewReport report = new ReviewReport();
        report.setReview(review);
        report.setReporter(reporter);
        report.setReason(reason);
        report.setDetails(details);
        report.setStatus(ReviewReport.ReportStatus.PENDING);

        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<ReviewReport> getPendingReports() {
        return reportRepository.findByStatus(ReviewReport.ReportStatus.PENDING);
    }

    @Transactional
    public void resolveReport(UUID reportPublicId, boolean deleteReview) {
        ReviewReport report = reportRepository.findByPublicId(reportPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));

        if (deleteReview) {
            reviewService.deleteReview(report.getReview().getPublicId());
            report.setStatus(ReviewReport.ReportStatus.RESOLVED);
        } else {
            report.setStatus(ReviewReport.ReportStatus.DISMISSED);
        }

        report.setResolvedAt(OffsetDateTime.now());
        reportRepository.save(report);
    }
}
