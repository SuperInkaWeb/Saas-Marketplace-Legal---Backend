package com.saas.legit.module.admin.controller;

import com.saas.legit.module.admin.dto.DocumentTemplateRequest;
import com.saas.legit.module.admin.dto.DocumentTemplateResponse;
import com.saas.legit.module.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/templates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDocumentTemplateController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<DocumentTemplateResponse>> getAllTemplates() {
        return ResponseEntity.ok(adminService.getAllDocumentTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentTemplateResponse> getTemplate(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getDocumentTemplate(id));
    }

    @PostMapping
    public ResponseEntity<DocumentTemplateResponse> createTemplate(@Valid @RequestBody DocumentTemplateRequest request) {
        return ResponseEntity.ok(adminService.createDocumentTemplate(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentTemplateResponse> updateTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody DocumentTemplateRequest request) {
        return ResponseEntity.ok(adminService.updateDocumentTemplate(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        adminService.deleteDocumentTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
