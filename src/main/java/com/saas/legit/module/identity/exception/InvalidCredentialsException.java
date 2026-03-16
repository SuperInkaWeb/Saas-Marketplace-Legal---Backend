package com.saas.legit.module.identity.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("El correo o la contraseña son incorrectos. Por favor, inténtalo de nuevo.");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}