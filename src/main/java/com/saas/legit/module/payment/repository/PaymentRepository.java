package com.saas.legit.module.payment.repository;

import com.saas.legit.module.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPublicId(UUID publicId);
    
    @Query("SELECT p FROM Payment p WHERE p.appointment.lawyerProfile.user.idUser = :userId ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByLawyerUserId(@Param("userId") Long userId);
}
