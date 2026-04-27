package com.saas.legit.module.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class AppointmentRequest {
    
    @NotNull(message = "El ID público del abogado es obligatorio")
    private UUID lawyerPublicId;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    @Future(message = "La hora de inicio debe ser en el futuro")
    private OffsetDateTime scheduledStart;
    
    @NotNull(message = "La hora de finalización es obligatoria")
    @Future(message = "La hora de finalización debe ser en el futuro")
    private OffsetDateTime scheduledEnd;
    
    private String notes;
}
