package com.saas.legit.module.marketplace.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.marketplace.dto.*;
import com.saas.legit.module.marketplace.service.LawyerProfileConfigService;
import com.saas.legit.module.marketplace.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;
    private final LawyerProfileConfigService lawyerProfileConfigService;

    // ── CASOS ABIERTOS ────────────────────────────────────────────────

    @GetMapping("/cases/open")
    public ResponseEntity<List<CaseRequestResponse>> getOpenCases() {
        return ResponseEntity.ok(marketplaceService.getOpenRequests());
    }

    @PostMapping("/cases/{casePublicId}/proposals")
    public ResponseEntity<LawyerProposalResponse> submitProposal(
            @PathVariable UUID casePublicId,
            @Valid @RequestBody CreateProposalRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        LawyerProposalResponse response = marketplaceService.submitProposal(userId, casePublicId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── BÚSQUEDA PÚBLICA DE ABOGADOS ──────────────────────────────────

    @GetMapping("/lawyers")
    public ResponseEntity<Page<LawyerSearchResponse>> searchLawyers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(lawyerProfileConfigService.searchLawyers(city, specialtyId, minRating, page, size));
    }

    @GetMapping("/lawyers/{slug}")
    public ResponseEntity<?> getPublicLawyerProfile(@PathVariable String slug) {
        return ResponseEntity.ok(lawyerProfileConfigService.getPublicProfile(slug));
    }

    @GetMapping("/specialties")
    public ResponseEntity<List<SpecialtyResponse>> getAllSpecialties() {
        return ResponseEntity.ok(lawyerProfileConfigService.getAllSpecialties());
    }
}
