package com.saas.legit.module.marketplace.controller;

import com.saas.legit.module.marketplace.dto.ReviewDTO;
import com.saas.legit.module.marketplace.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Endpoints for managing verified lawyer reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Submit a verified review for a completed appointment")
    public ResponseEntity<ReviewDTO> createReview(
            @AuthenticationPrincipal(expression = "idUser") Long userId,
            @RequestBody ReviewDTO.Create request) {
        return ResponseEntity.ok(reviewService.createReview(userId, request));
    }

    @GetMapping("/lawyer/{publicId}")
    @Operation(summary = "Get all reviews for a specific lawyer")
    public ResponseEntity<List<ReviewDTO>> getLawyerReviews(@PathVariable UUID publicId) {
        return ResponseEntity.ok(reviewService.getLawyerReviews(publicId));
    }
}
