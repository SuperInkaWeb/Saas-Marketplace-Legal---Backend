package com.saas.legit.module.ai.repository;

import com.saas.legit.module.ai.model.AiChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiChatSessionRepository extends JpaRepository<AiChatSession, Long> {
    
    Optional<AiChatSession> findByPublicId(UUID publicId);
    
    List<AiChatSession> findByUserIdUserOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT s FROM AiChatSession s WHERE s.createdAt < :date")
    List<AiChatSession> findSessionsOlderThan(OffsetDateTime date);
}
