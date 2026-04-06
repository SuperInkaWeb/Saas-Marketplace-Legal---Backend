package com.saas.legit.module.chat.model;

import com.saas.legit.module.appointment.model.Appointment;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.marketplace.model.CaseRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", updatable = false, nullable = false, unique = true)
    private UUID publicId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_request_id", unique = true)
    private CaseRequest caseRequest;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", unique = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_user_id", nullable = false)
    private User clientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_user_id", nullable = false)
    private User lawyerUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatRoomStatus status = ChatRoomStatus.ACTIVE;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (publicId == null) publicId = UUID.randomUUID();
    }

    /**
     * Check if this room has expired (>24h after closing).
     */
    public boolean isExpired() {
        if (closedAt == null) return false;
        return OffsetDateTime.now().isAfter(closedAt.plusHours(24));
    }

    /**
     * Get effective status considering the 24h expiration rule.
     */
    public ChatRoomStatus getEffectiveStatus() {
        if (status == ChatRoomStatus.FINISHED && isExpired()) {
            return ChatRoomStatus.EXPIRED;
        }
        return status;
    }
}
