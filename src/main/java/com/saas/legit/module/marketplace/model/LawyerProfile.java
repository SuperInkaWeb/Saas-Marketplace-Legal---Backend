package com.saas.legit.module.marketplace.model;

import com.saas.legit.module.catalog.model.LawFirm;
import com.saas.legit.module.identity.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lawyer_profiles")
@Getter
@Setter
@SQLRestriction("deleted_at IS NULL")
public class LawyerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idLawyerProfile;

    @Column(name = "public_id", updatable = false, nullable = false, unique = true)
    private UUID publicId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "law_firm_id")
    private LawFirm lawFirm;

    @Column(name = "slug", nullable = false, unique = true, length = 200)
    private String slugLawyerProfile;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bioLawyer;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @Column(name = "rating_avg", precision = 3, scale = 2)
    private BigDecimal ratingAvg = new BigDecimal("0.00");

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "bar_registration_number", length = 50)
    private String barRegistrationNumber;

    @Column(name = "bar_association", length = 150)
    private String barAssociation;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 20)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "search_vector", insertable = false, updatable = false, columnDefinition = "tsvector")
    private String searchVector;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public LawyerProfile(){
    }

    public LawyerProfile(User user, String slugLawyerProfile, String city, String country) {
        this.user = user;
        this.slugLawyerProfile = slugLawyerProfile;
        this.city = city;
        this.country = country;
        this.hourlyRate = BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        REJECTED
    }
}