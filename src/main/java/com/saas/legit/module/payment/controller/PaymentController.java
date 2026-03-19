package com.saas.legit.module.payment.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.payment.dto.PaymentResponse;
import com.saas.legit.module.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/lawyer")
    public ResponseEntity<List<PaymentResponse>> getLawyerPayments() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(paymentService.getLawyerPayments(userId));
    }
}
