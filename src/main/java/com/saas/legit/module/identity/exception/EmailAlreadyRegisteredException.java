package com.saas.legit.module.identity.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("Este correo ya está vinculado a una cuenta.");
    }
}