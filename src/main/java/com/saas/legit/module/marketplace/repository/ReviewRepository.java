package com.saas.legit.module.marketplace.repository;

import com.saas.legit.module.marketplace.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByPublicId(UUID publicId);
    List<Review> findByLawyerProfileIdLawyerProfileOrderByCreatedAtDesc(Long lawyerProfileId);
    boolean existsByAppointmentId(Long appointmentId);
}
