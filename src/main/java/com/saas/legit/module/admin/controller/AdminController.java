package com.saas.legit.module.admin.controller;

import com.saas.legit.module.admin.dto.*;
import com.saas.legit.module.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ── DASHBOARD ─────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardMetrics());
    }

    // ── USER MANAGEMENT ───────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserListResponse>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(adminService.getUsers(search, role, status, pageable));
    }

    @GetMapping("/users/{publicId}")
    public ResponseEntity<AdminUserDetailResponse> getUserDetail(@PathVariable UUID publicId) {
        return ResponseEntity.ok(adminService.getUserDetail(publicId));
    }

    @PutMapping("/users/{publicId}/status")
    public ResponseEntity<Void> updateAccountStatus(
            @PathVariable UUID publicId,
            @Valid @RequestBody UpdateAccountStatusRequest request
    ) {
        adminService.updateAccountStatus(publicId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{publicId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID publicId) {
        adminService.deleteUser(publicId);
        return ResponseEntity.noContent().build();
    }

    // ── LAWYER VERIFICATION ───────────────────────────────────────────

    @GetMapping("/lawyers/pending")
    public ResponseEntity<List<AdminLawyerPendingResponse>> getPendingLawyers() {
        return ResponseEntity.ok(adminService.getPendingLawyers());
    }

    @PutMapping("/lawyers/{userPublicId}/verify")
    public ResponseEntity<Void> verifyLawyer(
            @PathVariable UUID userPublicId,
            @Valid @RequestBody VerifyLawyerRequest request
    ) {
        adminService.verifyLawyer(userPublicId, request);
        return ResponseEntity.noContent().build();
    }
}
