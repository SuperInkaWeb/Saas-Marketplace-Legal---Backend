package com.saas.legit.module.marketplace.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.appointment.model.Appointment;
import com.saas.legit.module.appointment.model.AppointmentStatus;
import com.saas.legit.module.appointment.repository.AppointmentRepository;
import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import com.saas.legit.module.marketplace.dto.ReviewDTO;
import com.saas.legit.module.marketplace.exception.DuplicateReviewException;
import com.saas.legit.module.marketplace.exception.UnauthorizedAccessException;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.model.Review;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import com.saas.legit.module.marketplace.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
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
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        if (!appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new IllegalArgumentException("Solo se pueden calificar citas que hayan sido completadas");
        }

        if (!appointment.getClientProfile().getUser().getIdUser().equals(userId)) {
            throw new IllegalArgumentException("No estás autorizado para calificar esta cita");
        }

        if (reviewRepository.existsByAppointmentId(appointment.getId())) {
            throw new DuplicateReviewException("Ya existe una valoración para esta cita");
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

        lawyerProfileRepository.updateRatingAtomic(
                appointment.getLawyerProfile().getIdLawyerProfile(),
                request.getRating()
        );

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getLawyerReviews(UUID lawyerPublicId) {
        // Try finding by User Public ID (preferred for marketplace consistency)
        LawyerProfile lawyer = lawyerProfileRepository.findByUserPublicId(lawyerPublicId)
                .or(() -> lawyerProfileRepository.findByPublicId(lawyerPublicId))
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el abogado con el ID: " + lawyerPublicId));

        return reviewRepository.findByLawyerProfile_IdLawyerProfileOrderByCreatedAtDesc(lawyer.getIdLawyerProfile())
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO replyToReview(UUID reviewPublicId, ReviewDTO.ReplyRequest request, Long lawyerUserId) {
        Review review = reviewRepository.findByPublicId(reviewPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración no encontrada"));

        if (!review.getLawyerProfile().getUser().getIdUser().equals(lawyerUserId)) {
            throw new UnauthorizedAccessException("No estás autorizado para responder a esta valoración");
        }

        if (review.getReplyText() != null) {
            throw new DuplicateReviewException("Esta valoración ya tiene una respuesta");
        }

        review.setReplyText(request.getReplyText());
        review.setRepliedAt(OffsetDateTime.now());

        return mapToDTO(reviewRepository.save(review));
    }

    private ReviewDTO mapToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setPublicId(review.getPublicId());
        dto.setLawyerPublicId(review.getLawyerProfile().getPublicId());
        dto.setAppointmentPublicId(review.getAppointment().getPublicId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setIsAnonymous(review.getIsAnonymous());
        dto.setReplyText(review.getReplyText());
        dto.setRepliedAt(review.getRepliedAt());
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
