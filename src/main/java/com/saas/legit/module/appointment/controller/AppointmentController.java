package com.saas.legit.module.appointment.controller;

import com.saas.legit.core.util.SecurityUtils;
import com.saas.legit.module.appointment.dto.AppointmentRequest;
import com.saas.legit.module.appointment.dto.AppointmentResponse;
import com.saas.legit.module.appointment.model.AppointmentStatus;
import com.saas.legit.module.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        AppointmentResponse response = appointmentService.createAppointment(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/lawyer")
    public ResponseEntity<List<AppointmentResponse>> getLawyerAppointments() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(appointmentService.getLawyerAppointments(userId));
    }

    @GetMapping("/client")
    public ResponseEntity<List<AppointmentResponse>> getClientAppointments() {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(appointmentService.getClientAppointments(userId));
    }

    @PatchMapping("/{appointmentId}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable UUID appointmentId,
            @RequestParam AppointmentStatus status) {
        Long userId = SecurityUtils.getCurrentUser().userId();
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(userId, appointmentId, status));
    }
}
