package com.saas.legit.module.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChatRoomResponse {
    private UUID id;
    private String caseId;
    private String caseTitle;
    private String otherParticipantName;
    private String otherParticipantAvatar;
    private String lastMessage;
    private OffsetDateTime lastMessageAt;
    private long unreadCount;
    private String status;
    private OffsetDateTime closedAt;
}
