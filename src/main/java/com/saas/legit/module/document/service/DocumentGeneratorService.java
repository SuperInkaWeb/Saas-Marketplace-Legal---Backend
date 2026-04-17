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
                        t.getFieldDefinitions(),
                        t.getPrice()
                ))
                .toList();
    }

    // ── Private helpers ─────────────────────────────────────────────────

    /** Marker used to tag hidden/optional fields that should not be shown in the document */
    private static final String HIDDEN_MARKER = "__CAMPO_OCULTO__";

    /** Keys that are considered spouse-related and should be hidden when marital status is not CASADO/CONVIVIENTE */
    private static final java.util.Set<String> SPOUSE_KEYS = java.util.Set.of(
            "NOMBRE_CONYUGE", "NUM_DOC_CONYUGE", "DNI_CONYUGE",
            "APELLIDO_CONYUGE", "CONYUGE_NOMBRE", "CONYUGE_DNI", "CONYUGE_DOC",
            "NOMBRE_ESPOSA", "NOMBRE_ESPOSO"
    );

    /** Marital status values that require showing the spouse section */
    private static final java.util.Set<String> MARRIED_STATUSES = java.util.Set.of(
            "CASADO", "CASADA", "CONVIVIENTE"
    );

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

            // 2. Determine whether the marital status requires spouse section
            String maritalStatus = findMaritalStatus(userData);
            boolean showSpouseSection = maritalStatus == null || MARRIED_STATUSES.contains(maritalStatus.toUpperCase().trim());

            // 3. Build Thymeleaf context with user data
            Context ctx = new Context();
            for (String key : detectedKeys) {
                Object value = userData.get(key);
                boolean isSpouseKey = SPOUSE_KEYS.contains(key) ||
                        key.toUpperCase().contains("CONYUGE") ||
                        key.toUpperCase().contains("ESPOS");

                if (isSpouseKey && !showSpouseSection) {
                    // Hide spouse fields by injecting the removal marker
                    ctx.setVariable(key, HIDDEN_MARKER);
                } else if (value == null || value.toString().trim().isEmpty()) {
                    if (isSpouseKey) {
                        // Optional spouse field and no value — also mark for removal
                        ctx.setVariable(key, HIDDEN_MARKER);
                    } else {
                        missingFields.add(key);
                    }
                } else {
                    ctx.setVariable(key, value.toString().toUpperCase());
                }
            }

            // 4. Render with Thymeleaf
            String generatedContent = documentTemplateEngine.process(content, ctx);

            // 5. Post-process: remove any HTML block (<p>, <tr>, <li>) that contains the hidden marker
            generatedContent = removeHiddenBlocks(generatedContent);

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

    /** Finds the marital status value from userData, checking common key names */
    private String findMaritalStatus(Map<String, Object> userData) {
        for (java.util.Map.Entry<String, Object> entry : userData.entrySet()) {
            String key = entry.getKey().toUpperCase();
            if (key.contains("ESTADO_CIVIL") || key.contains("ESTADOCIVIL")) {
                Object val = entry.getValue();
                return val != null ? val.toString() : null;
            }
        }
        return null;
    }

    /**
     * Removes entire HTML block-level elements (<p>, <tr>, <li>, <div>) that contain the HIDDEN_MARKER.
     * This ensures conditional sections (like the spouse block) are fully removed from the generated document.
     */
    private String removeHiddenBlocks(String html) {
        // Remove <p> blocks containing the marker
        html = removeTagsContainingMarker(html, "p");
        // Remove <tr> blocks containing the marker (tables with spouse signature)
        html = removeTagsContainingMarker(html, "tr");
        // Remove <li> blocks containing the marker
        html = removeTagsContainingMarker(html, "li");
        // Remove any remaining standalone marker text
        html = html.replace(HIDDEN_MARKER, "");
        return html;
    }

    private String removeTagsContainingMarker(String html, String tag) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "<" + tag + "(?:[^>]*)>(?:(?!</" + tag + ">)[\\s\\S])*" + java.util.regex.Pattern.quote(HIDDEN_MARKER) + "(?:(?!</" + tag + ">)[\\s\\S])*</" + tag + ">",
                java.util.regex.Pattern.CASE_INSENSITIVE
        );
        return p.matcher(html).replaceAll("");
    }
}
