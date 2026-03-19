package com.saas.legit.module.marketplace.exception;

public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException() {
        super("El horario se solapa con otro existente para el mismo día");
    }
}
