package com.saas.legit.module.document.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.document.dto.DocumentTemplateDto;
import com.saas.legit.module.document.dto.DocumentGeneratorRequest;
import com.saas.legit.module.document.dto.DocumentGeneratorResponse;
import com.saas.legit.module.document.model.Document;
import com.saas.legit.module.document.model.DocumentTemplate;
import com.saas.legit.module.document.repository.DocumentRepository;
import com.saas.legit.module.document.repository.DocumentTemplateRepository;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.model.CaseRequest;
import com.saas.legit.module.marketplace.repository.CaseRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class DocumentGeneratorService {

    private final DocumentTemplateRepository templateRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final CaseRequestRepository caseRequestRepository;
    private final TemplateEngine documentTemplateEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DocumentGeneratorService(
            DocumentTemplateRepository templateRepository,
            DocumentRepository documentRepository,
            UserRepository userRepository,
            CaseRequestRepository caseRequestRepository,
            @Qualifier("documentTemplateEngine") TemplateEngine documentTemplateEngine) {
        this.templateRepository = templateRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.caseRequestRepository = caseRequestRepository;
        this.documentTemplateEngine = documentTemplateEngine;
    }

    /**
     * Preview-only: renders the template with the given data but does NOT persist anything.
     */
    @Transactional(readOnly = true)
    public DocumentGeneratorResponse previewDocument(DocumentGeneratorRequest request) {
        DocumentTemplate template = templateRepository.findByCode(request.getDocumentTypeCode())
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + request.getDocumentTypeCode()));

        return renderTemplate(template, request.getUserData());
    }

    /**
     * Full generation: renders, validates, and persists the document as a draft.
     */
    @Transactional
    public DocumentGeneratorResponse generateDocument(Long userId, DocumentGeneratorRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        DocumentTemplate template = templateRepository.findByCode(request.getDocumentTypeCode())
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + request.getDocumentTypeCode()));

        DocumentGeneratorResponse rendered = renderTemplate(template, request.getUserData());

        // Only persist if all fields are filled
        if (rendered.isValid()) {
            Document draftDoc = new Document();
            draftDoc.setUser(user);
            draftDoc.setFileName(template.getName() + " - " + request.getJurisdiction());
            draftDoc.setFileUrl("");
            draftDoc.setContent(rendered.getGeneratedContent());
            draftDoc.setIsDraft(false); // Mark as finalized since it's fully generated
            draftDoc.setFileType("html");
            
            // Calculate approximate size in bytes
            if (rendered.getGeneratedContent() != null) {
                draftDoc.setFileSizeBytes((long) rendered.getGeneratedContent().getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
            }

            if (request.getCaseRequestId() != null) {
                CaseRequest caseRequest = caseRequestRepository.findById(request.getCaseRequestId())
                        .orElseThrow(() -> new ResourceNotFoundException("CaseRequest not found"));
                draftDoc.setCaseRequest(caseRequest);
            }

            Document savedDraft = documentRepository.save(draftDoc);
            rendered.setDocumentPublicId(savedDraft.getPublicId());
        }

        return rendered;
    }

    @Transactional(readOnly = true)
    public List<DocumentTemplateDto> getActiveTemplates() {
        return templateRepository.findAll().stream()
                .filter(DocumentTemplate::getIsActive)
                .map(t -> new DocumentTemplateDto(
                        t.getPublicId(),
                        t.getName(),
                        t.getCode(),
                        t.getJurisdiction(),
                        t.getRequiredFields(),
                        t.getFieldDefinitions()
                ))
                .toList();
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private DocumentGeneratorResponse renderTemplate(DocumentTemplate template, Map<String, Object> userData) {
        try {
            String content = template.getContent();
            List<String> missingFields = new java.util.ArrayList<>();

            // 1. Detect all th:text="${VAR}" placeholders in the HTML content
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$\\{([A-Z_]+)\\}");
            java.util.regex.Matcher matcher = pattern.matcher(content);
            java.util.Set<String> detectedKeys = new java.util.LinkedHashSet<>();
            while (matcher.find()) {
                detectedKeys.add(matcher.group(1).trim());
            }

            // 2. Build Thymeleaf context with user data
            Context ctx = new Context();
            for (String key : detectedKeys) {
                Object value = userData.get(key);
                if (value == null || value.toString().trim().isEmpty()) {
                    missingFields.add(key);
                    // Leave key unset so Thymeleaf renders the original blank-line fallback
                } else {
                    ctx.setVariable(key, value.toString());
                }
            }

            // 3. Render with Thymeleaf
            String generatedContent = documentTemplateEngine.process(content, ctx);

            boolean isValid = missingFields.isEmpty();

            return DocumentGeneratorResponse.builder()
                    .generatedContent(generatedContent)
                    .missingFields(missingFields)
                    .isValid(isValid)
                    .documentPublicId(null)
                    .build();

        } catch (Exception e) {
            log.error("Error generating document with Thymeleaf", e);
            throw new RuntimeException("Error processing document template", e);
        }
    }
}
