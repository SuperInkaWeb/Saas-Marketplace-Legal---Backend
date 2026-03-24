package com.saas.legit.module.document.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.document.dto.DocumentResponse;
import com.saas.legit.module.document.dto.UploadDocumentRequest;
import com.saas.legit.module.document.model.Document;
import com.saas.legit.module.document.repository.DocumentRepository;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Transactional
    public DocumentResponse uploadDocument(Long userId, UploadDocumentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Document doc = new Document();
        doc.setUser(user);
        doc.setFileName(request.getFileName());
        doc.setFileUrl(request.getFileUrl());
        doc.setFileType(request.getFileType());
        doc.setFileSizeBytes(request.getFileSizeBytes());
        
        doc.setIsTemplate(request.getIsTemplate() != null ? request.getIsTemplate() : false);
        doc.setPrice(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO);

        Document saved = documentRepository.save(doc);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getMyDocuments(Long userId) {
        return documentRepository.findByUser_IdUserAndIsArchivedFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getAvailableTemplates() {
        return documentRepository.findByIsTemplateTrueAndIsArchivedFalseOrderByCreatedAtDesc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public void archiveDocument(Long userId, UUID documentPublicId) {
        Document doc = documentRepository.findByPublicId(documentPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!doc.getUser().getIdUser().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to archive this document");
        }

        doc.setIsArchived(true);
        documentRepository.save(doc);
    }

    private DocumentResponse mapToResponse(Document doc) {
        return DocumentResponse.builder()
                .publicId(doc.getPublicId())
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .fileType(doc.getFileType())
                .fileSizeBytes(doc.getFileSizeBytes())
                .isTemplate(doc.getIsTemplate())
                .price(doc.getPrice())
                .signatureStatus(doc.getSignatureStatus())
                .isArchived(doc.getIsArchived())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
