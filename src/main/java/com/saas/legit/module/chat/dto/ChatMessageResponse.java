package com.saas.legit.module.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChatMessageResponse {
    private UUID id;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private String text;
    private OffsetDateTime createdAt;
    private boolean isRead;
}
