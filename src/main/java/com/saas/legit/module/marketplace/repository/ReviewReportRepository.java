package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.marketplace.model.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    Optional<ReviewReport> findByPublicId(UUID publicId);
    List<ReviewReport> findByStatus(ReviewReport.ReportStatus status);
}
