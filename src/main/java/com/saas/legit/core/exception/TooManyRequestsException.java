package com.saas.legit.core.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {
        super("Has superado el límite de intentos. Por favor, espera unos minutos.");
    }
}