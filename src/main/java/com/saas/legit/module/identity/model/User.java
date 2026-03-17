package com.saas.legit.module.identity.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@SQLRestriction("deleted_at IS NULL")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idUser;

    @Column(name = "public_id", updatable = false, nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_secret", nullable = false)
    private String passwordSecret;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name_father", nullable = false, length = 100)
    private String lastNameFather;

    @Column(name = "last_name_mother", nullable = false, length = 100)
    private String lastNameMother;

    @Column(name = "phone", nullable = false, length = 20)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "onboarding_step", nullable = false, length = 30)
    private OnboardingStep onboardingStep = OnboardingStep.ACCOUNT_CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public User() {
    }

    public User(String email, String passwordSecret, String firstName,
                String lastNameFather, String lastNameMother, String phoneNumber) {
        this.email = email;
        this.passwordSecret = passwordSecret;
        this.firstName = firstName;
        this.lastNameFather = lastNameFather;
        this.lastNameMother = lastNameMother;
        this.phoneNumber = phoneNumber;
        this.accountStatus = AccountStatus.PENDING;
        this.onboardingStep = OnboardingStep.ACCOUNT_CREATED;
    }

    public String getFullName() {
        return firstName + " " + lastNameFather;
    }

    public String getRoleName() {
        return role != null ? role.getNameRol() : null;
    }

    public boolean hasRole(String roleName) {
        if (role == null || roleName == null) return false;
        String existingRole = role.getNameRol().toUpperCase();
        String targetRole = roleName.toUpperCase();

        String cleanExisting = existingRole.startsWith("ROLE_") ? existingRole.substring(5) : existingRole;
        String cleanTarget = targetRole.startsWith("ROLE_") ? targetRole.substring(5) : targetRole;

        return cleanExisting.equals(cleanTarget);
    }

    @PrePersist
    protected void onCreate() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }
}
