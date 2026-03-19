package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.marketplace.model.LawyerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}