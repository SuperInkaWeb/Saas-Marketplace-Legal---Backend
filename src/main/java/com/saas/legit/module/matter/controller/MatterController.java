package com.saas.legit.module.matter.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.matter.dto.MatterCreateRequest;
import com.saas.legit.module.matter.dto.MatterResponse;
import com.saas.legit.module.matter.service.MatterService;
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
public class MatterController {

    private final MatterService matterService;

    @PostMapping
    public ResponseEntity<MatterResponse> createMatter(@Valid @RequestBody MatterCreateRequest request) {
        Long lawyerId = SecurityUtils.getCurrentUser().userId();
        MatterResponse response = matterService.createMatter(lawyerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MatterResponse>> getMyMatters() {
        Long lawyerId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(matterService.getMyMatters(lawyerId));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<MatterResponse> getMatter(@PathVariable UUID publicId) {
        Long lawyerId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(matterService.getMatter(lawyerId, publicId));
    }

    @PatchMapping("/{publicId}/status")
    public ResponseEntity<MatterResponse> updateStatus(
            @PathVariable UUID publicId,
            @RequestBody java.util.Map<String, String> body) {
        Long lawyerId = SecurityUtils.getCurrentUser().userId();
        com.saas.legit.module.matter.model.MatterStatus status = 
            com.saas.legit.module.matter.model.MatterStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(matterService.updateStatus(lawyerId, publicId, status));
    }
}
