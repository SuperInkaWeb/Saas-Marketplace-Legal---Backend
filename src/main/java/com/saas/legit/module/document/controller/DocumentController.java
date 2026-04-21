package com.saas.legit.module.document.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.document.dto.DocumentResponse;
import com.saas.legit.module.document.dto.UploadDocumentRequest;
import com.saas.legit.module.document.service.DocumentService;
import com.saas.legit.module.document.service.DocumentGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final DocumentGeneratorService documentGeneratorService;

    @PostMapping
    public ResponseEntity<DocumentResponse> uploadDocument(@Valid @RequestBody UploadDocumentRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        DocumentResponse response = documentService.uploadDocument(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/upload-matter")
    public ResponseEntity<DocumentResponse> uploadMatterDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("matterPublicId") UUID matterPublicId) throws IOException {
        Long userId = SecurityUtils.getCurrentUser().userId();
        DocumentResponse response = documentService.uploadMatterDocument(userId, matterPublicId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadGeneralDocument(
            @RequestParam("file") MultipartFile file) throws IOException {
        Long userId = SecurityUtils.getCurrentUser().userId();
        DocumentResponse response = documentService.uploadGeneralDocument(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(documentService.getMyDocuments(userId));
    }

    @GetMapping("/matter/{matterPublicId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByMatter(@PathVariable UUID matterPublicId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(documentService.getDocumentsByMatter(userId, matterPublicId));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable UUID documentId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(documentService.getDocument(userId, documentId));
    }

    @GetMapping("/{documentId}/file")
    public ResponseEntity<byte[]> getDocumentFile(@PathVariable UUID documentId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return documentService.streamDocumentFile(userId, documentId);
    }

    @GetMapping("/templates")
    public ResponseEntity<List<com.saas.legit.module.document.dto.DocumentTemplateDto>> getTemplates() {
        return ResponseEntity.ok(documentGeneratorService.getActiveTemplates());
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> archiveDocument(@PathVariable UUID documentId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        documentService.archiveDocument(userId, documentId);
        return ResponseEntity.noContent().build();
    }
}
