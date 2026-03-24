package com.saas.legit.module.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {
    private UUID publicId;
    private String title;
    private String message;
    private Boolean isRead;
    private String actionUrl;
    private OffsetDateTime createdAt;
}
