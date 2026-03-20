package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.marketplace.model.LawyerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LawyerProfileRepository extends JpaRepository<LawyerProfile, Long> {

    Optional<LawyerProfile> findByPublicId(UUID publicId);

    Optional<LawyerProfile> findByUserIdUser(Long userId);

    boolean existsBySlugLawyerProfile(String slug);

    @Query("SELECT lp FROM LawyerProfile lp WHERE lp.user.idUser = :userId")
    Optional<LawyerProfile> findByUserId(@Param("userId") Long userId);

    @Query("SELECT lp FROM LawyerProfile lp WHERE lp.slugLawyerProfile = :slug")
    Optional<LawyerProfile> findBySlug(@Param("slug") String slug);

    @Query("""
            SELECT DISTINCT lp FROM LawyerProfile lp
            LEFT JOIN lp.specialties s
            WHERE lp.isVerified = true
            AND (:city IS NULL OR LOWER(lp.city) = LOWER(:city))
            AND (:specialtyId IS NULL OR s.id = :specialtyId)
            AND (:minRating IS NULL OR lp.ratingAvg >= :minRating)
            ORDER BY lp.ratingAvg DESC
            """)
    Page<LawyerProfile> searchVerifiedLawyers(
            @Param("city") String city,
            @Param("specialtyId") Long specialtyId,
            @Param("minRating") BigDecimal minRating,
            Pageable pageable
    );
}