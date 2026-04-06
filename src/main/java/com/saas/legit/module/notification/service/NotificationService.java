package com.saas.legit.module.notification.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.notification.dto.NotificationResponse;
import com.saas.legit.module.notification.model.Notification;
import com.saas.legit.module.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findByUser_IdUserOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long userId, UUID publicId) {
        Notification notification = notificationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        if (!notification.getUser().getIdUser().equals(userId)) {
            throw new IllegalArgumentException("Not authorized");
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUser_IdUserAndIsReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .publicId(notification.getPublicId())
                .title(notification.getTitle())
                .type(notification.getType().name())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .actionUrl(notification.getActionUrl())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
