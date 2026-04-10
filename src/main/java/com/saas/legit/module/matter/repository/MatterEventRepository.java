package com.saas.legit.module.matter.repository;

import com.saas.legit.module.matter.model.MatterEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatterEventRepository extends JpaRepository<MatterEvent, Long> {
    Optional<MatterEvent> findByPublicId(UUID publicId);
    List<MatterEvent> findByMatter_IdOrderByEventDateDesc(Long matterId);
}
