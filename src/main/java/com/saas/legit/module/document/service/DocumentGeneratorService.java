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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentGeneratorService {

    private final DocumentTemplateRepository templateRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final CaseRequestRepository caseRequestRepository;

    private static final Pattern STATIC_PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    @Transactional
    public DocumentGeneratorResponse generateDocument(Long userId, DocumentGeneratorRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        DocumentTemplate template = templateRepository.findByCode(request.getDocumentTypeCode())
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + request.getDocumentTypeCode()));

        String content = template.getContent();
        Map<String, Object> userData = request.getUserData();
        List<String> missingFields = new ArrayList<>();

        // Process placeholders format: {{FIELD_NAME}}
        Matcher matcher = STATIC_PLACEHOLDER_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String fieldName = matcher.group(1).trim();
            Object valueObj = userData.get(fieldName);

            String replacement;
            if (valueObj != null && !valueObj.toString().trim().isEmpty()) {
                replacement = valueObj.toString();
            } else {
                replacement = "[COMPLETAR: " + fieldName + "]";
                missingFields.add(fieldName);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        String generatedContent = sb.toString();
        boolean isValid = missingFields.isEmpty();

        UUID savedId = null;
        
        if (isValid) {
            Document draftDoc = new Document();
            draftDoc.setUser(user);
            draftDoc.setFileName(template.getName() + " - " + request.getJurisdiction());
            draftDoc.setFileUrl("");
            draftDoc.setContent(generatedContent);
            draftDoc.setIsDraft(true);
            draftDoc.setFileType("markdown");

            if (request.getCaseRequestId() != null) {
                CaseRequest caseRequest = caseRequestRepository.findById(request.getCaseRequestId())
                        .orElseThrow(() -> new ResourceNotFoundException("CaseRequest not found"));
                draftDoc.setCaseRequest(caseRequest);
            }

            Document savedDraft = documentRepository.save(draftDoc);
            savedId = savedDraft.getPublicId();
        }

        return DocumentGeneratorResponse.builder()
                .generatedContent(generatedContent)
                .missingFields(missingFields)
                .isValid(isValid)
                .documentPublicId(savedId)
                .build();
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
                        t.getRequiredFields()
                ))
                .toList();
    }
}
