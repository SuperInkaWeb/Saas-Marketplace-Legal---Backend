package com.saas.legit.module.document.repository;

import com.saas.legit.module.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByPublicId(UUID publicId);
    List<Document> findByUser_IdUserAndIsArchivedFalseOrderByCreatedAtDesc(Long userId);
    List<Document> findByIsTemplateTrueAndIsArchivedFalseOrderByCreatedAtDesc();
}
