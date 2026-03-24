package com.saas.legit.module.notification.repository;

import com.saas.legit.module.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdUserOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUser_IdUserAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    java.util.Optional<Notification> findByPublicId(UUID publicId);
}
