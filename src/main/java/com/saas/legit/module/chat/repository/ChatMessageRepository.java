package com.saas.legit.module.chat.repository;

import com.saas.legit.module.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoom_IdOrderByCreatedAtAsc(Long chatRoomId);

    /**
     * Count unread messages in a specific room for a given user (messages NOT sent by that user).
     */
    @Query("""
        SELECT COUNT(m) FROM ChatMessage m
        WHERE m.chatRoom.id = :roomId
          AND m.sender.idUser <> :userId
          AND m.isRead = false
    """)
    long countUnreadInRoom(@Param("roomId") Long roomId, @Param("userId") Long userId);

    /**
     * Total unread messages across all rooms for a given user.
     */
    @Query("""
        SELECT COUNT(m) FROM ChatMessage m
        WHERE m.chatRoom.id IN (
            SELECT r.id FROM ChatRoom r
            WHERE r.clientUser.idUser = :userId OR r.lawyerUser.idUser = :userId
        )
        AND m.sender.idUser <> :userId
        AND m.isRead = false
    """)
    long countTotalUnread(@Param("userId") Long userId);

    /**
     * Mark all messages in a room as read (only those not sent by the current user).
     */
    @Modifying
    @Query("""
        UPDATE ChatMessage m SET m.isRead = true
        WHERE m.chatRoom.id = :roomId
          AND m.sender.idUser <> :userId
          AND m.isRead = false
    """)
    void markAllAsReadInRoom(@Param("roomId") Long roomId, @Param("userId") Long userId);

    /**
     * Find the most recent message in a room.
     */
    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.chatRoom.id = :roomId
        ORDER BY m.createdAt DESC
        LIMIT 1
    """)
    ChatMessage findLastMessageInRoom(@Param("roomId") Long roomId);
}
