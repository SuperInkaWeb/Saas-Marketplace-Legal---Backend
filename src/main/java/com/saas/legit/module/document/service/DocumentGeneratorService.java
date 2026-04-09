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

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentGeneratorService {

    private final DocumentTemplateRepository templateRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final CaseRequestRepository caseRequestRepository;
    private final Handlebars handlebars = new Handlebars();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public DocumentGeneratorResponse generateDocument(Long userId, DocumentGeneratorRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        DocumentTemplate template = templateRepository.findByCode(request.getDocumentTypeCode())
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + request.getDocumentTypeCode()));

        String content = template.getContent();
        Map<String, Object> userData = new java.util.HashMap<>(request.getUserData());
        List<String> missingFields = new java.util.ArrayList<>();

        try {
            // 1. Escanear todos los marcadores {{VARIABLE}} en el contenido
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{\\{([^{}#/^]+)\\}\\}");
            java.util.regex.Matcher matcher = pattern.matcher(content);
            java.util.Set<String> detectedKeys = new java.util.HashSet<>();
            while (matcher.find()) {
                detectedKeys.add(matcher.group(1).trim());
            }

            // 2. Mapear nombres técnicos a etiquetas amigables (Labels) si existen
            Map<String, String> fieldLabels = new java.util.HashMap<>();
            if (template.getFieldDefinitions() != null) {
                List<Map<String, Object>> defs = objectMapper.readValue(template.getFieldDefinitions(), new TypeReference<>() {});
                for (Map<String, Object> def : defs) {
                    fieldLabels.put((String) def.get("name"), (String) def.get("label"));
                }
            }

            // 3. Crear mapa normalizado para búsqueda ultra-robusta (insensible a espacios, guiones y caso)
            java.util.Map<String, Object> normalizedData = new java.util.HashMap<>();
            userData.forEach((k, v) -> normalizedData.put(normalizeKey(k), v));

            // 4. Validar cada clave detectada contra los datos del usuario
            for (String key : detectedKeys) {
                String normKey = normalizeKey(key);
                Object value = normalizedData.get(normKey);
                
                if (value == null || value.toString().trim().isEmpty()) {
                    missingFields.add(key);
                    String friendlyLabel = fieldLabels.getOrDefault(key, key.replace("_", " "));
                    userData.put(key, "[COMPLETAR: " + friendlyLabel + "]");
                } else {
                    // Asegurar que el valor esté en el mapa original con la clave exacta que espera Handlebars
                    userData.put(key, value);
                }
            }

            // Renderizar con Handlebars
            Template handlebarsTemplate = handlebars.compileInline(content);
            String generatedContent = handlebarsTemplate.apply(userData);
            
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

        } catch (Exception e) {
            log.error("Error generating document with Handlebars", e);
            throw new RuntimeException("Error processing document template", e);
        }
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

    private String normalizeKey(String key) {
        if (key == null) return "";
        return key.toUpperCase()
                .replace(" ", "")
                .replace("_", "")
                .trim();
    }
}
