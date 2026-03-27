package com.saas.legit.module.appointment.exception;

public class InvalidAppointmentTransitionException extends RuntimeException {
    public InvalidAppointmentTransitionException(String from, String to) {
        super("Transición de estado no permitida: " + from + " → " + to);
    }
}