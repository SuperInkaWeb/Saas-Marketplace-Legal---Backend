package com.saas.legit.module.chat.repository;

import com.saas.legit.module.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByPublicId(UUID publicId);

    Optional<ChatRoom> findByCaseRequest_Id(Long caseRequestId);

    Optional<ChatRoom> findByAppointment_Id(Long appointmentId);

    @Query("""
        SELECT r FROM ChatRoom r
        WHERE r.clientUser.idUser = :userId OR r.lawyerUser.idUser = :userId
        ORDER BY r.createdAt DESC
    """)
    List<ChatRoom> findAllByParticipant(@Param("userId") Long userId);
}
