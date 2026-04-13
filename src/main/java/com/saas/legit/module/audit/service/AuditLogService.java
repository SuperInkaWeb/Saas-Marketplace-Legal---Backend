package com.saas.legit.module.audit.service;

import com.saas.legit.module.audit.model.AuditLog;
import com.saas.legit.module.audit.repository.AuditLogRepository;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    /**
     * Log an audit event. Uses REQUIRES_NEW so audit always persists even if outer tx rolls back.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String entityType, Long entityId, String action, Long userId,
                    String oldValue, String newValue, String description) {
        try {
            AuditLog entry = new AuditLog();
            entry.setEntityType(entityType);
            entry.setEntityId(entityId);
            entry.setAction(action);
            entry.setOldValue(oldValue);
            entry.setNewValue(newValue);
            entry.setDescription(description);

            if (userId != null) {
                userRepository.findById(userId).ifPresent(entry::setUser);
            }

            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to write audit log: {}", e.getMessage());
        }
    }
}
