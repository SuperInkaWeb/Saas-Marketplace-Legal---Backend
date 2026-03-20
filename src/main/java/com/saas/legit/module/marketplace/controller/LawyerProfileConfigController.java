package com.saas.legit.module.marketplace.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.identity.dto.PublicProfileResponse;
import com.saas.legit.module.identity.dto.UserMeResponse;
import com.saas.legit.module.identity.service.UserService;
import com.saas.legit.module.marketplace.dto.*;
import com.saas.legit.module.marketplace.service.LawyerProfileConfigService;
import com.saas.legit.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lawyer-profile")
@RequiredArgsConstructor
public class LawyerProfileConfigController {

    private final LawyerProfileConfigService configService;
    private final UserService userService;

    // ── CREAR PERFIL (onboarding) ──────────────────────────────────────

    @PostMapping
    public ResponseEntity<UserMeResponse> createLawyerProfile(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal,
            @Valid @RequestBody CreateLawyerProfileRequest request
    ) {
        configService.createLawyerProfile(principal.userId(), request);
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }

    @GetMapping("/status")
    public ResponseEntity<UserMeResponse> getOnboardingStatus(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserDetails principal
    ) {
        return ResponseEntity.ok(userService.getMe(principal.userId()));
    }

    // ── CONFIG COMPLETO ────────────────────────────────────────────────

    @GetMapping("/config")
    public ResponseEntity<LawyerProfileConfigResponse> getMyConfig() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(configService.getMyProfileConfig(userId));
    }

    // ── ACTUALIZAR PERFIL ──────────────────────────────────────────────

    @PutMapping("/update")
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody UpdateLawyerProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        configService.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    // ── PERFIL PÚBLICO ─────────────────────────────────────────────────

    @GetMapping("/public/{slug}")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@PathVariable String slug) {
        return ResponseEntity.ok(configService.getPublicProfile(slug));
    }

    // ── ESPECIALIDADES ─────────────────────────────────────────────────

    @GetMapping("/specialties/all")
    public ResponseEntity<List<SpecialtyResponse>> getAllSpecialties() {
        return ResponseEntity.ok(configService.getAllSpecialties());
    }

    @PutMapping("/specialties")
    public ResponseEntity<Void> updateSpecialties(@Valid @RequestBody UpdateSpecialtiesRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        configService.updateSpecialties(userId, request);
        return ResponseEntity.ok().build();
    }

    // ── HORARIOS ───────────────────────────────────────────────────────

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleResponse>> getMySchedules() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(configService.getMySchedules(userId));
    }

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleResponse> addSchedule(@Valid @RequestBody ScheduleRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        ScheduleResponse response = configService.addSchedule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(configService.updateSchedule(userId, id, request));
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        configService.deleteSchedule(userId, id);
        return ResponseEntity.noContent().build();
    }

    // ── RESEÑAS ───────────────────────────────────────────────────────

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponse>> getMyReviews() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(configService.getMyReviews(userId));
    }

    // ── PROPUESTAS ────────────────────────────────────────────────────

    @GetMapping("/proposals")
    public ResponseEntity<List<LawyerProposalResponse>> getMyProposals() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(configService.getMyProposals(userId));
    }

    // ── DASHBOARD STATS ───────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(configService.getDashboardStats(userId));
    }
}
