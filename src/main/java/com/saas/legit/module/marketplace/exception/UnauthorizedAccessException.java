package com.saas.legit.module.marketplace.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String resource) {
        super("No tienes permiso para acceder a este recurso: " + resource);
    }
}
