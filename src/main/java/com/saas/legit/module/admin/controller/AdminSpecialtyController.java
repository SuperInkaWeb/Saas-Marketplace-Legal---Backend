package com.saas.legit.module.admin.controller;

import com.saas.legit.module.admin.dto.CreateSpecialtyRequest;
import com.saas.legit.module.admin.service.AdminService;
import com.saas.legit.module.marketplace.dto.SpecialtyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/specialties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSpecialtyController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> getAllSpecialties() {
        return ResponseEntity.ok(adminService.getAllSpecialties());
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponse> createSpecialty(@Valid @RequestBody CreateSpecialtyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createSpecialty(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> updateSpecialty(
            @PathVariable Long id,
            @Valid @RequestBody CreateSpecialtyRequest request
    ) {
        return ResponseEntity.ok(adminService.updateSpecialty(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Long id) {
        adminService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<SpecialtyResponse> toggleSpecialtyStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleSpecialtyStatus(id));
    }
}
