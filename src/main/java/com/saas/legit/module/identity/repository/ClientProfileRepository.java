package com.saas.legit.module.identity.repository;

import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {

    Optional<ClientProfile> findByUser(User user);
    Optional<ClientProfile> findByUser_IdUser(Long userId);
    Optional<ClientProfile> findByPublicId(java.util.UUID publicId);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM ClientProfile c JOIN c.user u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))")
    java.util.List<ClientProfile> searchClients(@org.springframework.data.repository.query.Param("query") String query);

    boolean existsByUser(User user);
}
