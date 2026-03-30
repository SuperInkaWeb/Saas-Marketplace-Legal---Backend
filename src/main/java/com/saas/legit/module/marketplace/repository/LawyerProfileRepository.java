package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.identity.model.IdentityDocument;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LawyerProfileRepository extends JpaRepository<LawyerProfile, Long> {

    Optional<LawyerProfile> findByPublicId(UUID publicId);

    @Query("SELECT lp FROM LawyerProfile lp WHERE lp.user.publicId = :publicId")
    Optional<LawyerProfile> findByUserPublicId(@Param("publicId") UUID publicId);

    Optional<LawyerProfile> findByUserIdUser(Long userId);

    boolean existsBySlugLawyerProfile(String slug);

    // ── Admin queries ─────────────────────────────────────────────────

    @Query("SELECT lp FROM LawyerProfile lp WHERE lp.user.idUser IN :userIds")
    List<LawyerProfile> findByUserIdIn(@Param("userIds") List<Long> userIds);

    List<LawyerProfile> findByVerificationStatus(LawyerProfile.VerificationStatus status);

    long countByVerificationStatus(LawyerProfile.VerificationStatus status);

    @Query("SELECT lp FROM LawyerProfile lp WHERE lp.user.idUser = :userId")
    Optional<LawyerProfile> findByUserId(@Param("userId") Long userId);

    @Query("SELECT lp FROM LawyerProfile lp WHERE lp.slugLawyerProfile = :slug")
    Optional<LawyerProfile> findBySlug(@Param("slug") String slug);

    @Query("""
            SELECT DISTINCT lp FROM LawyerProfile lp
            LEFT JOIN lp.specialties s
            WHERE lp.isVerified = true
            AND EXISTS (
                SELECT 1 FROM IdentityDocument id 
                WHERE id.user.idUser = lp.user.idUser 
                AND id.isVerified = true
            )
            AND (CAST(:query AS string) IS NULL OR (
                LOWER(lp.city) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR
                LOWER(lp.user.firstName) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR
                LOWER(lp.user.lastNameFather) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
            ))
            AND (:specialtyId IS NULL OR s.id = :specialtyId)
            AND (:minRating IS NULL OR lp.ratingAvg >= :minRating)
            ORDER BY lp.ratingAvg DESC
            """)
    Page<LawyerProfile> searchVerifiedLawyers(
            @Param("query") String query,
            @Param("specialtyId") Long specialtyId,
            @Param("minRating") BigDecimal minRating,
            Pageable pageable
    );

    @Modifying
    @Query("""
        UPDATE LawyerProfile lp SET
            lp.reviewCount = lp.reviewCount + 1,
            lp.ratingAvg   = ROUND(
                (lp.ratingAvg * lp.reviewCount + :newScore) / (lp.reviewCount + 1),
                2
            )
        WHERE lp.idLawyerProfile = :lawyerProfileId
        """)
    void updateRatingAtomic(
            @Param("lawyerProfileId") Long lawyerProfileId,
            @Param("newScore") Short newScore
    );
}