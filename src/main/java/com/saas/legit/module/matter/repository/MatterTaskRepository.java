package com.saas.legit.module.matter.repository;

import com.saas.legit.module.matter.model.MatterTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatterTaskRepository extends JpaRepository<MatterTask, Long> {
    Optional<MatterTask> findByPublicId(UUID publicId);
    List<MatterTask> findByMatter_IdOrderByDueDateAsc(Long matterId);
}
