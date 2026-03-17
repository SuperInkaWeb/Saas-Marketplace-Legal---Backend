package com.saas.legit.module.identity.exception;

public class UnauthorizedKycAccessException extends RuntimeException {

    public UnauthorizedKycAccessException() {
        super("No tienes permisos para realizar esta acción de verificación.");
    }
}
