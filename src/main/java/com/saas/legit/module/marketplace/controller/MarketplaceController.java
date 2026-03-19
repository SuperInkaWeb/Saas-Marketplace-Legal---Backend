package com.saas.legit.module.marketplace.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.marketplace.dto.CaseRequestResponse;
import com.saas.legit.module.marketplace.dto.CreateProposalRequest;
import com.saas.legit.module.marketplace.dto.LawyerProposalResponse;
import com.saas.legit.module.marketplace.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

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
}
