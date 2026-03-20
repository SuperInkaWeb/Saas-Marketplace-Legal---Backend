package com.saas.legit.module.marketplace.exception;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException() {
        super("Horario no encontrado");
    }
}
