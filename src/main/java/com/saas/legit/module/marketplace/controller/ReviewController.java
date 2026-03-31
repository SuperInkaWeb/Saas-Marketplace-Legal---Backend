package com.saas.legit.module.marketplace.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.marketplace.dto.ReviewDTO;
import com.saas.legit.module.marketplace.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO.Create request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(reviewService.createReview(userId, request));
    }

    @GetMapping("/lawyer/{publicId}")
    public ResponseEntity<List<ReviewDTO>> getLawyerReviews(@PathVariable UUID publicId) {
        return ResponseEntity.ok(reviewService.getLawyerReviews(publicId));
    }

    @PatchMapping("/{publicId}/reply")
    public ResponseEntity<ReviewDTO> replyToReview(
            @PathVariable UUID publicId,
            @Valid @RequestBody ReviewDTO.ReplyRequest request) {
        Long lawyerUserId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(reviewService.replyToReview(publicId, request, lawyerUserId));
    }

    @PatchMapping("/{publicId}/feature")
    public ResponseEntity<ReviewDTO> toggleFeatured(@PathVariable UUID publicId) {
        Long lawyerUserId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(reviewService.toggleFeatured(publicId, lawyerUserId));
    }
}
