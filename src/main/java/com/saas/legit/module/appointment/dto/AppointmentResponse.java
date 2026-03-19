package com.saas.legit.module.appointment.dto;

import com.saas.legit.module.appointment.model.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AppointmentResponse {
    private UUID publicId;
    private UUID clientPublicId;
    private String clientName;
    private UUID lawyerPublicId;
    private String lawyerName;
    private OffsetDateTime scheduledStart;
    private OffsetDateTime scheduledEnd;
    private AppointmentStatus status;
    private String meetingLink;
    private String notes;
}
