package com.saas.legit.module.marketplace.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.marketplace.dto.*;
import com.saas.legit.module.marketplace.service.LawyerProfileConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lawyer-profile")
@RequiredArgsConstructor
public class LawyerProfileConfigController {

    private final LawyerProfileConfigService configService;

    // ── CONFIG COMPLETO ────────────────────────────────────────────────

    @GetMapping("/config")
    public ResponseEntity<LawyerProfileConfigResponse> getMyConfig() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(configService.getMyProfileConfig(userId));
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

}
