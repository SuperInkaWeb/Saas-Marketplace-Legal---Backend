package com.saas.legit.module.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusySlotResponse {
    private OffsetDateTime start;
    private OffsetDateTime end;
}
