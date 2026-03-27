package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.marketplace.model.LawyerProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LawyerProposalRepository extends JpaRepository<LawyerProposal, Long> {
    List<LawyerProposal> findByCaseRequest_IdOrderByCreatedAtDesc(Long caseRequestId);
    List<LawyerProposal> findByLawyerProfile_IdLawyerProfileOrderByCreatedAtDesc(Long lawyerProfileId);
    Optional<LawyerProposal> findByCaseRequest_IdAndLawyerProfile_IdLawyerProfile(Long caseRequestId, Long lawyerProfileId);

    @Modifying
    @Query("""
        UPDATE LawyerProposal p SET p.status = 'REJECTED'
        WHERE p.caseRequest.id = :caseRequestId
        AND p.id != :acceptedProposalId
        AND p.status = 'PENDING'
        """)
    void rejectOtherProposals(
            @Param("caseRequestId") Long caseRequestId,
            @Param("acceptedProposalId") Long acceptedProposalId
    );
}
