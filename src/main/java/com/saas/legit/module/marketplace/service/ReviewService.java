package com.saas.legit.module.marketplace.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.appointment.model.Appointment;
import com.saas.legit.module.appointment.model.AppointmentStatus;
import com.saas.legit.module.appointment.repository.AppointmentRepository;
import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import com.saas.legit.module.marketplace.dto.ReviewDTO;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.model.Review;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import com.saas.legit.module.marketplace.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final LawyerProfileRepository lawyerProfileRepository;

    @Transactional
    public ReviewDTO createReview(Long userId, ReviewDTO.Create request) {
        // 1. Validate Appointment
        Appointment appointment = appointmentRepository.findByPublicId(request.getAppointmentPublicId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new IllegalArgumentException("Only completed appointments can be reviewed");
        }

        if (!appointment.getClientProfile().getUser().getIdUser().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to review this appointment");
        }

        if (reviewRepository.existsByAppointmentId(appointment.getId())) {
            throw new IllegalArgumentException("A review already exists for this appointment");
        }

        // 2. Create Review
        Review review = new Review();
        review.setAppointment(appointment);
        review.setClientProfile(appointment.getClientProfile());
        review.setLawyerProfile(appointment.getLawyerProfile());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setIsAnonymous(request.getIsAnonymous());

        Review saved = reviewRepository.save(review);

        // 3. Update Lawyer Profile Rating
        updateLawyerRating(appointment.getLawyerProfile(), request.getRating());

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getLawyerReviews(UUID lawyerPublicId) {
        LawyerProfile lawyer = lawyerProfileRepository.findByPublicId(lawyerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found"));

        return reviewRepository.findByLawyerProfile_IdLawyerProfileOrderByCreatedAtDesc(lawyer.getIdLawyerProfile())
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void updateLawyerRating(LawyerProfile lawyer, Short newScore) {
        BigDecimal currentAvg = lawyer.getRatingAvg() != null ? lawyer.getRatingAvg() : BigDecimal.ZERO;
        int currentCount = lawyer.getReviewCount() != null ? lawyer.getReviewCount() : 0;

        BigDecimal totalSum = currentAvg.multiply(new BigDecimal(currentCount));
        BigDecimal newTotalSum = totalSum.add(new BigDecimal(newScore));
        int newCount = currentCount + 1;

        BigDecimal newAvg = newTotalSum.divide(new BigDecimal(newCount), 2, RoundingMode.HALF_UP);

        lawyer.setRatingAvg(newAvg);
        lawyer.setReviewCount(newCount);
        lawyerProfileRepository.save(lawyer);
    }

    private ReviewDTO mapToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setPublicId(review.getPublicId());
        dto.setLawyerPublicId(review.getLawyerProfile().getPublicId());
        dto.setAppointmentPublicId(review.getAppointment().getPublicId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setIsAnonymous(review.getIsAnonymous());
        dto.setCreatedAt(review.getCreatedAt());

        if (review.getIsAnonymous()) {
            dto.setClientName("Usuario Anónimo");
        } else {
            dto.setClientName(review.getClientProfile().getUser().getFirstName() + " " +
                    review.getClientProfile().getUser().getLastNameFather().charAt(0) + ".");
        }

        return dto;
    }
}
