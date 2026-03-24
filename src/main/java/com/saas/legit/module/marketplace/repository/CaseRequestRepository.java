package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.marketplace.model.CaseRequest;
import com.saas.legit.module.marketplace.model.CaseRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseRequestRepository extends JpaRepository<CaseRequest, Long> {
    Optional<CaseRequest> findByPublicId(UUID publicId);
    List<CaseRequest> findByClientProfile_IdClientProfileOrderByCreatedAtDesc(Long clientProfileId);
    List<CaseRequest> findByStatusOrderByCreatedAtDesc(CaseRequestStatus status);
}
