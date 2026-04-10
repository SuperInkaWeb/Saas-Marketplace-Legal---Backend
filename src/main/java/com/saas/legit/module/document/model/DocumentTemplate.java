package com.saas.legit.module.document.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_templates")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class DocumentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", updatable = false, nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", nullable = false, length = 100, unique = true)
    private String code; // e.g. "NDA_STANDARD", "SERVICE_AGREEMENT"

    @Column(name = "jurisdiction", nullable = false, length = 100)
    private String jurisdiction;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // Markdown or HTML representation containing placeholders like [COMPLETAR: NOMBRE]

    @Column(name = "required_fields", columnDefinition = "TEXT")
    private String requiredFields; // Comma-separated list of required fields for validation

    @Column(name = "field_definitions", columnDefinition = "TEXT")
    private String fieldDefinitions; // JSON string with structured field metadata (type, options, validation)

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "price", precision = 10, scale = 2)
    private java.math.BigDecimal price;

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
