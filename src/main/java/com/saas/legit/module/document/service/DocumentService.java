package com.saas.legit.module.document.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.core.service.CloudinaryService;
import com.saas.legit.module.document.dto.DocumentResponse;
import com.saas.legit.module.document.dto.UploadDocumentRequest;
import com.saas.legit.module.document.model.Document;
import com.saas.legit.module.document.repository.DocumentRepository;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.matter.model.Matter;
import com.saas.legit.module.matter.repository.MatterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final MatterRepository matterRepository;
    private final CloudinaryService cloudinaryService;
    private final DocumentPdfService documentPdfService;
    private final RestTemplate restTemplate = new RestTemplate();

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

        if (request.getMatterPublicId() != null) {
            Matter matter = matterRepository.findByPublicId(request.getMatterPublicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Matter not found"));
            doc.setMatter(matter);
        }

        Document saved = documentRepository.save(doc);
        return mapToResponse(saved);
    }

    @Transactional
    public DocumentResponse uploadMatterDocument(Long userId, UUID matterPublicId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Matter matter = matterRepository.findByPublicId(matterPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Matter not found"));

        // Upload to Cloudinary
        String fileUrl = cloudinaryService.uploadFile(file, "matters/" + matterPublicId);

        Document doc = new Document();
        doc.setUser(user);
        doc.setMatter(matter);
        doc.setFileName(file.getOriginalFilename());
        doc.setFileUrl(fileUrl);
        doc.setFileType(file.getContentType());
        doc.setFileSizeBytes(file.getSize());
        doc.setIsDraft(false);

        Document saved = documentRepository.save(doc);
        return mapToResponse(saved);
    }

    @Transactional
    public DocumentResponse uploadGeneralDocument(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Upload to Cloudinary - User personal vault folder
        String folder = "users/" + userId + "/vault";
        String fileUrl = cloudinaryService.uploadFile(file, folder);

        Document doc = new Document();
        doc.setUser(user);
        doc.setFileName(file.getOriginalFilename());
        doc.setFileUrl(fileUrl);
        doc.setFileType(file.getContentType());
        doc.setFileSizeBytes(file.getSize());
        doc.setIsDraft(false);
        doc.setIsTemplate(false);

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

    @Transactional(readOnly = true)
    public DocumentResponse getDocument(Long userId, UUID documentPublicId) {
        Document doc = documentRepository.findByPublicId(documentPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!isAuthorized(userId, doc)) {
            throw new IllegalArgumentException("Not authorized to access this document");
        }

        return mapToResponse(doc);
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

    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByMatter(Long userId, UUID matterPublicId) {
        // In a real scenario, we should check if the lawyer has access to this matter
        return documentRepository.findByMatter_PublicIdAndIsArchivedFalseOrderByCreatedAtDesc(matterPublicId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> streamDocumentFile(Long userId, UUID documentPublicId) {
        Document doc = documentRepository.findByPublicId(documentPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        // Basic authorization: check if owner or has access to matter
        if (!isAuthorized(userId, doc)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] fileBytes;
        String contentType = doc.getFileType() != null ? doc.getFileType() : "application/octet-stream";
        String fileName = doc.getFileName() != null ? doc.getFileName() : "document";

        try {
            String url = doc.getFileUrl();
            if (doc.getContent() != null && (url == null || url.isEmpty())) {
                // Generated HTML document -> Convert to PDF for proxy viewing
                fileBytes = documentPdfService.generatePdfFromHtml(doc.getContent(), fileName);
                contentType = "application/pdf";
                if (!fileName.toLowerCase().endsWith(".pdf")) fileName += ".pdf";
            } else if (url != null && !url.isEmpty()) {
                // Uploaded document (Cloudinary or other) -> Proxy the fetch
                log.info("Proxying file from URL: {}", url);
                
                if (url.contains("cloudinary.com")) {
                    // Use authenticated Cloudinary download to bypass 401s
                    fileBytes = cloudinaryService.downloadPrivateFile(url);
                } else {
                    fileBytes = restTemplate.getForObject(url, byte[].class);
                }
                
                if (fileBytes == null) throw new RuntimeException("Could not fetch file from storage (empty response)");
            } else {
                throw new RuntimeException("Document has no content and no URL");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());
            
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            log.error("Authentication error fetching from Cloudinary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Error proxying document file: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isAuthorized(Long userId, Document doc) {
        // Owner
        if (doc.getUser().getIdUser().equals(userId)) return true;

        // Matter associated: check lawyer or client
        if (doc.getMatter() != null) {
            Matter matter = doc.getMatter();
            
            // Lawyer of the matter
            if (matter.getLawyer() != null && matter.getLawyer().getIdUser().equals(userId)) {
                return true;
            }
            
            // Client of the matter
            if (matter.getClient() != null && matter.getClient().getUser() != null && 
                matter.getClient().getUser().getIdUser().equals(userId)) {
                return true;
            }
        }
        
        return false;
    }

    private DocumentResponse mapToResponse(Document doc) {
        return DocumentResponse.builder()
                .publicId(doc.getPublicId())
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .fileType(doc.getFileType())
                .fileSizeBytes(doc.getFileSizeBytes())
                .isTemplate(doc.getIsTemplate())
                .isDraft(doc.getIsDraft())
                .content(doc.getContent())
                .price(doc.getPrice())
                .signatureStatus(doc.getSignatureStatus())
                .isArchived(doc.getIsArchived())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
