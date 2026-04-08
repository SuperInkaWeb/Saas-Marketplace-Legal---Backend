package com.saas.legit.module.document.controller;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.document.dto.DocumentGeneratorRequest;
import com.saas.legit.module.document.dto.DocumentGeneratorResponse;
import com.saas.legit.module.document.dto.DocumentUpdateRequest;
import com.saas.legit.module.document.model.Document;
import com.saas.legit.module.document.repository.DocumentRepository;
import com.saas.legit.module.document.service.DocumentGeneratorService;
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
            // Note: In a real system, you'd check if the user is the lawyer handling the case request
            throw new IllegalArgumentException("Not authorized to update this document");
        }

        doc.setContent(request.getContent());
        
        // If there are no more placeholders, it might be ready to exit draft mode
        if (!request.getContent().contains("[COMPLETAR:")) {
            doc.setIsDraft(false);
        }
        
        documentRepository.save(doc);

        return ResponseEntity.ok().build();
    }
}
