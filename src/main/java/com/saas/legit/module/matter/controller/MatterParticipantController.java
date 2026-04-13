package com.saas.legit.module.matter.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.matter.dto.MatterParticipantRequest;
import com.saas.legit.module.matter.dto.MatterParticipantResponse;
import com.saas.legit.module.matter.service.MatterParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matters")
@RequiredArgsConstructor
public class MatterParticipantController {

    private final MatterParticipantService participantService;

    @PostMapping("/{matterPublicId}/participants")
    public ResponseEntity<MatterParticipantResponse> addParticipant(
            @PathVariable UUID matterPublicId,
            @Valid @RequestBody MatterParticipantRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.addParticipant(matterPublicId, request, userId));
    }

    @GetMapping("/{matterPublicId}/participants")
    public ResponseEntity<List<MatterParticipantResponse>> getParticipants(
            @PathVariable UUID matterPublicId) {
        return ResponseEntity.ok(participantService.getParticipants(matterPublicId));
    }

    @PutMapping("/participants/{participantPublicId}")
    public ResponseEntity<MatterParticipantResponse> updateParticipant(
            @PathVariable UUID participantPublicId,
            @Valid @RequestBody MatterParticipantRequest request) {
        return ResponseEntity.ok(participantService.updateParticipant(participantPublicId, request));
    }

    @DeleteMapping("/participants/{participantPublicId}")
    public ResponseEntity<Void> removeParticipant(@PathVariable UUID participantPublicId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        participantService.removeParticipant(participantPublicId, userId);
        return ResponseEntity.noContent().build();
    }
}
