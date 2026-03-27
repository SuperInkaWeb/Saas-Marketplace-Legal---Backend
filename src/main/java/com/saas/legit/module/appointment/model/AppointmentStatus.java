package com.saas.legit.module.appointment.model;

import java.util.Map;
import java.util.Set;

public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    NO_SHOW;

    private static final Map<AppointmentStatus, Set<AppointmentStatus>> CLIENT_TRANSITIONS = Map.of(
            PENDING, Set.of(CANCELLED)
    );

    private static final Map<AppointmentStatus, Set<AppointmentStatus>> LAWYER_TRANSITIONS = Map.of(
            PENDING,    Set.of(CONFIRMED, CANCELLED),
            CONFIRMED,  Set.of(COMPLETED, NO_SHOW, CANCELLED)
    );

    public boolean isClientAllowed(AppointmentStatus next) {
        return CLIENT_TRANSITIONS.getOrDefault(this, Set.of()).contains(next);
    }

    public boolean isLawyerAllowed(AppointmentStatus next) {
        return LAWYER_TRANSITIONS.getOrDefault(this, Set.of()).contains(next);
    }
}
