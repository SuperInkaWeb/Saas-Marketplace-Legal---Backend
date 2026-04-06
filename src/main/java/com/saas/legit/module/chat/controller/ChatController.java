package com.saas.legit.module.chat.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.chat.dto.ChatMessageResponse;
import com.saas.legit.module.chat.dto.ChatRoomResponse;
import com.saas.legit.module.chat.dto.SendMessageRequest;
import com.saas.legit.module.chat.dto.UnreadCountResponse;
import com.saas.legit.module.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getRooms() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(chatService.getRoomsForUser(userId));
    }

    @GetMapping("/{roomPublicId}")
    public ResponseEntity<ChatRoomResponse> getRoomById(@PathVariable UUID roomPublicId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(chatService.getRoomById(userId, roomPublicId));
    }

    @GetMapping("/{roomPublicId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable UUID roomPublicId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(chatService.getMessages(userId, roomPublicId));
    }

    @PostMapping("/{roomPublicId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable UUID roomPublicId,
            @Valid @RequestBody SendMessageRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(chatService.sendMessage(userId, roomPublicId, request));
    }

    @PutMapping("/{roomPublicId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID roomPublicId) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        chatService.markAsRead(userId, roomPublicId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(chatService.getUnreadCount(userId));
    }
}
