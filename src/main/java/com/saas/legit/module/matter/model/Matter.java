package com.saas.legit.module.matter.model;

import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.marketplace.model.CaseRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "matters")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class Matter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", updatable = false, nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "number", length = 100, unique = true)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private MatterStatus status = MatterStatus.OPEN;

    @Column(name = "jurisdiction", length = 100)
    private String jurisdiction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id")
    private User lawyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientProfile client;

    @Column(name = "unregistered_client_name")
    private String unregisteredClientName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_request_id")
    private CaseRequest caseRequest;

    @Column(name = "start_date")
    private OffsetDateTime startDate = OffsetDateTime.now();

    @Column(name = "estimated_end_date")
    private OffsetDateTime estimatedEndDate;

    @OneToMany(mappedBy = "matter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatterEvent> events = new ArrayList<>();

    @OneToMany(mappedBy = "matter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatterTask> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "matter")
    private List<com.saas.legit.module.document.model.Document> documents = new ArrayList<>();

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
