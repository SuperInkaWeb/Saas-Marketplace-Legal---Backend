package com.saas.legit.module.identity.repository;

import com.saas.legit.module.identity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByPublicId(UUID publicId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    // ── Admin queries ─────────────────────────────────────────────────

    long countByRole_NameRol(String roleName);

    long countByCreatedAtAfter(OffsetDateTime date);

    @Query("""
            SELECT u FROM User u LEFT JOIN u.role r
            WHERE (:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                   OR LOWER(u.lastNameFather) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
            AND (:role IS NULL OR r.nameRol = :role)
            AND (:status IS NULL OR CAST(u.accountStatus AS string) = :status)
            """)
    Page<User> searchUsers(
            @Param("search") String search,
            @Param("role") String role,
            @Param("status") String status,
            Pageable pageable
    );
}
