package com.saas.legit.module.notification.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.notification.dto.NotificationResponse;
import com.saas.legit.module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PutMapping("/{publicId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID publicId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        notificationService.markAsRead(userId, publicId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
