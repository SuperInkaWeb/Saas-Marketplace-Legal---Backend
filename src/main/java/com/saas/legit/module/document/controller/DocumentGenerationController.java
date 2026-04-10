package com.saas.legit.module.document.controller;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.document.dto.DocumentGeneratorRequest;
import com.saas.legit.module.document.dto.DocumentGeneratorResponse;
import com.saas.legit.module.document.dto.DocumentUpdateRequest;
import com.saas.legit.module.document.model.Document;
import com.saas.legit.module.document.repository.DocumentRepository;
import com.saas.legit.module.document.service.DocumentGeneratorService;
import com.saas.legit.module.document.service.DocumentPdfService;
import com.saas.legit.security.CustomUserDetailsService.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentGenerationController {

    private final DocumentGeneratorService documentGeneratorService;
    private final DocumentRepository documentRepository;
    private final DocumentPdfService documentPdfService;

    /**
     * Live Preview: renders the HTML template with user data without saving.
     * Called on every keystroke (debounced) from the frontend.
     */
    @PostMapping("/preview")
    public ResponseEntity<DocumentGeneratorResponse> previewDocument(
            @Valid @RequestBody DocumentGeneratorRequest request) {

        DocumentGeneratorResponse response = documentGeneratorService.previewDocument(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Generate and persist the document as a draft.
     */
    @PostMapping("/generate")
    public ResponseEntity<DocumentGeneratorResponse> generateDocument(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DocumentGeneratorRequest request) {

        DocumentGeneratorResponse response = documentGeneratorService.generateDocument(userDetails.userId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/content")
    public ResponseEntity<Void> updateDocumentContent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody DocumentUpdateRequest request) {

        Document doc = documentRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!doc.getUser().getIdUser().equals(userDetails.userId())) {
            throw new IllegalArgumentException("Not authorized to update this document");
        }

        doc.setContent(request.getContent());
        doc.setIsDraft(false);
        
        // Update size in bytes
        if (request.getContent() != null) {
            doc.setFileSizeBytes((long) request.getContent().getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        }

        documentRepository.save(doc);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportDocument(@PathVariable UUID id) {
        Document doc = documentRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        byte[] pdfBytes = documentPdfService.generatePdfFromHtml(doc.getContent(), doc.getFileName());

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"" + doc.getFileName() + ".pdf\"")
                .body(pdfBytes);
    }
}
