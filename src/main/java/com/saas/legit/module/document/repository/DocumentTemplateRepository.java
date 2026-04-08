package com.saas.legit.module.document.repository;

import com.saas.legit.module.document.model.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {
    Optional<DocumentTemplate> findByPublicId(UUID publicId);
    Optional<DocumentTemplate> findByCode(String code);
}
