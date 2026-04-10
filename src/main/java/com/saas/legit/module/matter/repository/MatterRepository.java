package com.saas.legit.module.matter.repository;

import com.saas.legit.module.matter.model.Matter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatterRepository extends JpaRepository<Matter, Long> {
    Optional<Matter> findByPublicId(UUID publicId);
    List<Matter> findByLawyer_IdUserOrderByCreatedAtDesc(Long lawyerId);
    List<Matter> findByClient_IdClientProfileOrderByCreatedAtDesc(Long clientId);
}
