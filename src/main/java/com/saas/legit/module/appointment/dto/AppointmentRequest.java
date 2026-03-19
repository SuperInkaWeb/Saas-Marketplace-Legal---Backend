package com.saas.legit.module.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class AppointmentRequest {
    
    @NotNull(message = "Lawyer public ID is required")
    private UUID lawyerPublicId;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private OffsetDateTime scheduledStart;
    
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private OffsetDateTime scheduledEnd;
    
    private String notes;
}
