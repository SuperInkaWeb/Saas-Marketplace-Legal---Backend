package com.saas.legit.module.marketplace.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.marketplace.dto.CaseWithProposalsResponse;
import com.saas.legit.module.marketplace.dto.CreateCaseRequest;
import com.saas.legit.module.marketplace.service.ClientCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
public class ClientCaseController {

    private final ClientCaseService clientCaseService;

    @PostMapping
    public ResponseEntity<CaseWithProposalsResponse> createCase(
            @Valid @RequestBody CreateCaseRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clientCaseService.createCase(userId, request));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<CaseWithProposalsResponse>> getMyCases() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(clientCaseService.getMyCases(userId));
    }

    @PatchMapping("/{casePublicId}/proposals/{proposalId}/accept")
    public ResponseEntity<Void> acceptProposal(
            @PathVariable UUID casePublicId,
            @PathVariable Long proposalId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        clientCaseService.acceptProposal(userId, casePublicId, proposalId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{casePublicId}/close")
    public ResponseEntity<Void> closeCase(@PathVariable UUID casePublicId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        clientCaseService.closeCase(userId, casePublicId);
        return ResponseEntity.noContent().build();
    }
}