package com.saas.legit.module.document.model;

import com.saas.legit.module.appointment.model.Appointment;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.marketplace.model.CaseRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", updatable = false, nullable = false, unique = true)
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_request_id")
    private CaseRequest caseRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matter_id")
    private com.saas.legit.module.matter.model.Matter matter;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(columnDefinition = "TEXT")
    private String content; // Draft markdown or HTML before PDF conversion

    @Column(name = "is_draft")
    private Boolean isDraft = true;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "is_template")
    private Boolean isTemplate = false;

    @Column(precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "signature_status", length = 50)
    private SignatureStatus signatureStatus = SignatureStatus.NOT_REQUIRED;

    @Column(name = "external_signature_id", length = 255)
    private String externalSignatureId;

    @Column(name = "is_archived")
    private Boolean isArchived = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (publicId == null) publicId = UUID.randomUUID();
    }
}
