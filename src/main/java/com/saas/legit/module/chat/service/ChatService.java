package com.saas.legit.module.chat.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.chat.dto.ChatMessageResponse;
import com.saas.legit.module.chat.dto.ChatRoomResponse;
import com.saas.legit.module.chat.dto.SendMessageRequest;
import com.saas.legit.module.chat.dto.UnreadCountResponse;
import com.saas.legit.module.chat.model.ChatMessage;
import com.saas.legit.module.chat.model.ChatRoom;
import com.saas.legit.module.chat.model.ChatRoomStatus;
import com.saas.legit.module.chat.repository.ChatMessageRepository;
import com.saas.legit.module.chat.repository.ChatRoomRepository;
import com.saas.legit.module.appointment.model.Appointment;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.model.CaseRequest;
import com.saas.legit.module.notification.model.Notification;
import com.saas.legit.module.notification.model.NotificationType;
import com.saas.legit.module.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ── GET ROOMS ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getRoomsForUser(Long userId) {
        List<ChatRoom> rooms = chatRoomRepository.findAllByParticipant(userId);

        return rooms.stream()
                .map(room -> mapToRoomResponse(room, userId))
                .toList();
    }

    // ── GET ROOM BY ID ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ChatRoomResponse getRoomById(Long userId, UUID roomPublicId) {
        ChatRoom room = findRoomAndValidateParticipant(userId, roomPublicId);
        return mapToRoomResponse(room, userId);
    }

    // ── GET MESSAGES ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long userId, UUID roomPublicId) {
        ChatRoom room = findRoomAndValidateParticipant(userId, roomPublicId);

        return chatMessageRepository.findByChatRoom_IdOrderByCreatedAtAsc(room.getId())
                .stream()
                .map(this::mapToMessageResponse)
                .toList();
    }

    // ── SEND MESSAGE ────────────────────────────────────────────────────

    @Transactional
    public ChatMessageResponse sendMessage(Long userId, UUID roomPublicId, SendMessageRequest request) {
        ChatRoom room = findRoomAndValidateParticipant(userId, roomPublicId);

        // Verify the room is still active (or within 24h grace period)
        if (room.getEffectiveStatus() == ChatRoomStatus.EXPIRED) {
            throw new IllegalStateException("Esta conversación ha expirado. No se pueden enviar más mensajes.");
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setText(request.text());

        ChatMessage saved = chatMessageRepository.save(message);

        // Create notification for the other participant
        createMessageNotification(room, sender);

        ChatMessageResponse response = mapToMessageResponse(saved);

        // Real-time broadcast to the room
        messagingTemplate.convertAndSend("/topic/chat/" + room.getPublicId(), response);

        // Real-time notification count update for the recipient
        User recipient = room.getClientUser().getIdUser().equals(sender.getIdUser()) ? room.getLawyerUser() : room.getClientUser();
        messagingTemplate.convertAndSend("/topic/unread-count/" + recipient.getPublicId(), getUnreadCount(recipient.getIdUser()));

        return response;
    }

    // ── MARK AS READ ────────────────────────────────────────────────────

    @Transactional
    public void markAsRead(Long userId, UUID roomPublicId) {
        ChatRoom room = findRoomAndValidateParticipant(userId, roomPublicId);
        chatMessageRepository.markAllAsReadInRoom(room.getId(), userId);
    }

    // ── UNREAD COUNT ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(Long userId) {
        long count = chatMessageRepository.countTotalUnread(userId);
        return new UnreadCountResponse(count);
    }

    // ── CREATE ROOM (called from ClientCaseService) ─────────────────────

    @Transactional
    public ChatRoom createRoom(CaseRequest caseRequest, User clientUser, User lawyerUser) {
        // Check if room already exists for this case
        if (chatRoomRepository.findByCaseRequest_Id(caseRequest.getId()).isPresent()) {
            throw new IllegalStateException("Ya existe un chat para este caso");
        }

        ChatRoom room = new ChatRoom();
        room.setCaseRequest(caseRequest);
        room.setClientUser(clientUser);
        room.setLawyerUser(lawyerUser);
        room.setStatus(ChatRoomStatus.ACTIVE);

        return chatRoomRepository.save(room);
    }

    // ── FINISH ROOM (called when case is closed) ────────────────────────

    @Transactional
    public void finishRoom(Long caseRequestId) {
        chatRoomRepository.findByCaseRequest_Id(caseRequestId).ifPresent(room -> {
            room.setStatus(ChatRoomStatus.FINISHED);
            room.setClosedAt(OffsetDateTime.now());
            chatRoomRepository.save(room);
        });
    }

    // ── CREATE APPOINTMENT ROOM ─────────────────────────────────────────

    @Transactional
    public ChatRoom createAppointmentRoom(Appointment appointment) {
        if (chatRoomRepository.findByAppointment_Id(appointment.getId()).isPresent()) {
            throw new IllegalStateException("Ya existe un chat para esta cita");
        }

        ChatRoom room = new ChatRoom();
        room.setAppointment(appointment);
        room.setClientUser(appointment.getClientProfile().getUser());
        room.setLawyerUser(appointment.getLawyerProfile().getUser());
        room.setStatus(ChatRoomStatus.ACTIVE);

        return chatRoomRepository.save(room);
    }

    // ── FINISH APPOINTMENT ROOM ─────────────────────────────────────────

    @Transactional
    public void finishAppointmentRoom(Long appointmentId) {
        chatRoomRepository.findByAppointment_Id(appointmentId).ifPresent(room -> {
            room.setStatus(ChatRoomStatus.FINISHED);
            room.setClosedAt(OffsetDateTime.now());
            chatRoomRepository.save(room);
        });
    }

    // ── PRIVATE HELPERS ─────────────────────────────────────────────────

    private ChatRoom findRoomAndValidateParticipant(Long userId, UUID roomPublicId) {
        ChatRoom room = chatRoomRepository.findByPublicId(roomPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat no encontrado"));

        boolean isParticipant = room.getClientUser().getIdUser().equals(userId)
                || room.getLawyerUser().getIdUser().equals(userId);

        if (!isParticipant) {
            throw new IllegalArgumentException("No tienes acceso a este chat");
        }

        return room;
    }

    private ChatRoomResponse mapToRoomResponse(ChatRoom room, Long currentUserId) {
        // Determine "the other" participant
        boolean isClient = room.getClientUser().getIdUser().equals(currentUserId);
        User otherUser = isClient ? room.getLawyerUser() : room.getClientUser();

        // Get last message
        ChatMessage lastMsg = chatMessageRepository.findLastMessageInRoom(room.getId());
        long unread = chatMessageRepository.countUnreadInRoom(room.getId(), currentUserId);

        String contextId;
        String contextTitle;
        if (room.getCaseRequest() != null) {
            contextId = room.getCaseRequest().getPublicId().toString();
            contextTitle = room.getCaseRequest().getTitle();
        } else {
            contextId = room.getAppointment().getPublicId().toString();
            contextTitle = "Cita de Asesoría";
        }

        return ChatRoomResponse.builder()
                .id(room.getPublicId())
                .caseId(contextId)
                .caseTitle(contextTitle)
                .otherParticipantName(otherUser.getFullName())
                .otherParticipantAvatar(otherUser.getAvatarURL())
                .lastMessage(lastMsg != null ? lastMsg.getText() : null)
                .lastMessageAt(lastMsg != null ? lastMsg.getCreatedAt() : null)
                .unreadCount(unread)
                .status(room.getEffectiveStatus().name())
                .closedAt(room.getClosedAt())
                .build();
    }

    private ChatMessageResponse mapToMessageResponse(ChatMessage msg) {
        User sender = msg.getSender();
        return ChatMessageResponse.builder()
                .id(msg.getPublicId())
                .senderId(sender.getPublicId().toString())
                .senderName(sender.getFullName())
                .senderAvatar(sender.getAvatarURL())
                .text(msg.getText())
                .createdAt(msg.getCreatedAt())
                .isRead(msg.getIsRead())
                .build();
    }

    private void createMessageNotification(ChatRoom room, User sender) {
        // Determine the recipient (the other participant)
        User recipient = room.getClientUser().getIdUser().equals(sender.getIdUser())
                ? room.getLawyerUser()
                : room.getClientUser();

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(NotificationType.CHAT_MESSAGE);
        notification.setTitle("Nuevo mensaje de " + sender.getFullName());

        String contextMsg = room.getCaseRequest() != null
                ? room.getCaseRequest().getTitle()
                : "Cita de Asesoría";

        notification.setMessage("Tienes un nuevo mensaje en: " + contextMsg);
        notification.setActionUrl("/dashboard/chats");

        notificationRepository.save(notification);
    }
}
