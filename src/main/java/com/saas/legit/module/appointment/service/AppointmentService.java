package com.saas.legit.module.appointment.service;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.appointment.dto.AppointmentRequest;
import com.saas.legit.module.appointment.dto.AppointmentResponse;
import com.saas.legit.module.appointment.model.Appointment;
import com.saas.legit.module.appointment.model.AppointmentStatus;
import com.saas.legit.module.appointment.repository.AppointmentRepository;
import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final LawyerProfileRepository lawyerProfileRepository;

    @Transactional
    public AppointmentResponse createAppointment(Long userId, AppointmentRequest request) {
        ClientProfile clientProfile = clientProfileRepository.findByUser_IdUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        LawyerProfile lawyerProfile = lawyerProfileRepository.findByPublicId(request.getLawyerPublicId())
                .orElseThrow(() -> new ResourceNotFoundException("Lawyer profile not found"));

        // Check for double booking
        boolean hasOverlap = appointmentRepository.hasOverlappingAppointments(
                lawyerProfile.getIdLawyerProfile(),
                request.getScheduledStart(),
                request.getScheduledEnd());

        if (hasOverlap) {
            throw new IllegalArgumentException("The lawyer already has an appointment during this time slot");
        }

        Appointment appointment = new Appointment();
        appointment.setClientProfile(clientProfile);
        appointment.setLawyerProfile(lawyerProfile);
        appointment.setScheduledStart(request.getScheduledStart());
        appointment.setScheduledEnd(request.getScheduledEnd());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getLawyerAppointments(Long userId) {
        LawyerProfile lawyerProfile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found"));

        return appointmentRepository.findByLawyerProfile_IdLawyerProfileOrderByScheduledStartDesc(lawyerProfile.getIdLawyerProfile())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getClientAppointments(Long userId) {
        ClientProfile clientProfile = clientProfileRepository.findByUser_IdUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        return appointmentRepository.findByClientProfile_IdClientProfileOrderByScheduledStartDesc(clientProfile.getIdClientProfile())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long userId, UUID appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findByPublicId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Basic authorization check: must belong to client or lawyer
        boolean isLawyer = appointment.getLawyerProfile().getUser().getIdUser().equals(userId);
        boolean isClient = appointment.getClientProfile().getUser().getIdUser().equals(userId);

        if (!isLawyer && !isClient) {
            throw new IllegalArgumentException("Not authorized to update this appointment");
        }

        appointment.setStatus(newStatus);

        if (newStatus == AppointmentStatus.CONFIRMED && appointment.getMeetingLink() == null) {
            appointment.setMeetingLink("https://meet.jit.si/legit-" + appointment.getPublicId());
        }

        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .publicId(appointment.getPublicId())
                .clientPublicId(appointment.getClientProfile().getPublicId())
                .clientName(appointment.getClientProfile().getUser().getFirstName() + " " + appointment.getClientProfile().getUser().getLastNameFather())
                .lawyerPublicId(appointment.getLawyerProfile().getPublicId())
                .lawyerName(appointment.getLawyerProfile().getUser().getFirstName() + " " + appointment.getLawyerProfile().getUser().getLastNameFather())
                .scheduledStart(appointment.getScheduledStart())
                .scheduledEnd(appointment.getScheduledEnd())
                .status(appointment.getStatus())
                .meetingLink(appointment.getMeetingLink())
                .notes(appointment.getNotes())
                .build();
    }
}
