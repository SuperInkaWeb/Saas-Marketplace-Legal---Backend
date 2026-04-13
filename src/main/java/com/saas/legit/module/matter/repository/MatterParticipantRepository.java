package com.saas.legit.module.matter.repository;

import com.saas.legit.module.matter.model.MatterParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatterParticipantRepository extends JpaRepository<MatterParticipant, Long> {
    Optional<MatterParticipant> findByPublicId(UUID publicId);
    List<MatterParticipant> findByMatter_IdOrderByCreatedAtDesc(Long matterId);
    long countByMatter_Id(Long matterId);
}
