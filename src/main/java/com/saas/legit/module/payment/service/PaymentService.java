package com.saas.legit.module.payment.service;

import com.saas.legit.module.payment.dto.PaymentResponse;
import com.saas.legit.module.payment.model.Payment;
import com.saas.legit.module.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public List<PaymentResponse> getLawyerPayments(Long userId) {
        return paymentRepository.findPaymentsByLawyerUserId(userId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .publicId(payment.getPublicId())
                .clientName(payment.getAppointment() != null ? 
                            payment.getAppointment().getClientProfile().getUser().getFirstName() + " " + 
                            payment.getAppointment().getClientProfile().getUser().getLastNameFather() : "Desconocido")
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
