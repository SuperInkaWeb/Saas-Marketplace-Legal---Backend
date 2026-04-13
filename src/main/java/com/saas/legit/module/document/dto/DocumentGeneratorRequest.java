package com.saas.legit.module.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class DocumentGeneratorRequest {

    // Identifies the DocumentTemplate by code (e.g. NDA_STANDARD)
    @NotBlank(message = "Document template code is required")
    private String documentTypeCode;

    // Optional CaseRequest to link this document with a CRM digital file
    private Long caseRequestId;
    
    @NotBlank(message = "Jurisdiction is required")
    private String jurisdiction;

    @NotNull(message = "User data map is required")
    private Map<String, Object> userData;
}
