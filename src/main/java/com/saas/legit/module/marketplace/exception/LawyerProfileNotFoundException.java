package com.saas.legit.module.marketplace.exception;

public class LawyerProfileNotFoundException extends RuntimeException {
    public LawyerProfileNotFoundException() {
        super("Perfil de abogado no encontrado");
    }
}
