package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.marketplace.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByPublicId(UUID publicId);
    
    @Query("SELECT r FROM Review r WHERE r.lawyerProfile.idLawyerProfile = :lawyerId ORDER BY r.isFeatured DESC, r.createdAt DESC")
    List<Review> findByLawyerIdOrderByFeaturedFirst(@Param("lawyerId") Long lawyerId);
    
    List<Review> findByLawyerProfile_IdLawyerProfileOrderByCreatedAtDesc(Long lawyerProfileId);
    boolean existsByAppointmentId(Long appointmentId);

    @Modifying
    @Query("UPDATE Review r SET r.isFeatured = false WHERE r.lawyerProfile.id = :lawyerId")
    void clearFeaturedForLawyer(@Param("lawyerId") Long lawyerId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.lawyerProfile.id = :lawyerId GROUP BY r.rating")
    List<Object[]> countRatingsByLawyerId(@Param("lawyerId") Long lawyerId);
}
