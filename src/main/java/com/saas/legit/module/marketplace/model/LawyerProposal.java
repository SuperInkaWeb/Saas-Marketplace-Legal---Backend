package com.saas.legit.module.marketplace.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "lawyer_proposals", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"case_request_id", "lawyer_profile_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class LawyerProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_request_id", nullable = false)
    private CaseRequest caseRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_profile_id", nullable = false)
    private LawyerProfile lawyerProfile;

    @Column(name = "proposal_text", nullable = false, columnDefinition = "TEXT")
    private String proposalText;

    @Column(name = "proposed_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal proposedFee;

    @Column(length = 10)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProposalStatus status = ProposalStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
