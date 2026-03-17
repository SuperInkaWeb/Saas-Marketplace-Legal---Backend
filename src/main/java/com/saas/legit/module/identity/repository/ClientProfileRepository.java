package com.saas.legit.module.identity.repository;

import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {

    Optional<ClientProfile> findByUser(User user);

    boolean existsByUser(User user);
}
