package com.saas.legit.module.ai.service;

import com.saas.legit.module.ai.model.AiChatSession;
import com.saas.legit.module.ai.repository.AiChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiSessionCleanupTask {

    private final AiChatSessionRepository sessionRepository;

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    @Transactional
    public void cleanupOldSessions() {
        log.info("Starting cleanup of old AI chat sessions...");
        OffsetDateTime threshold = OffsetDateTime.now().minusHours(24);
        
        List<AiChatSession> oldSessions = sessionRepository.findSessionsOlderThan(threshold);
        
        if (!oldSessions.isEmpty()) {
            sessionRepository.deleteAll(oldSessions);
            log.info("Deleted {} old AI chat sessions.", oldSessions.size());
        } else {
            log.info("No old AI chat sessions to delete.");
        }
    }
}
