package com.saas.legit.module.identity.repository;

import com.saas.legit.module.identity.model.IdentityDocument;
import com.saas.legit.module.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument, Long> {

    Optional<IdentityDocument> findByUser(User user);

    boolean existsByUser(User user);
}
