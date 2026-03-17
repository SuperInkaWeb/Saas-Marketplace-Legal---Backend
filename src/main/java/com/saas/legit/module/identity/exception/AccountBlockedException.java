package com.saas.legit.module.identity.exception;

public class AccountBlockedException extends RuntimeException {

    public AccountBlockedException() {
        super("Tu cuenta ha sido bloqueada. Contacta al soporte para más información.");
    }
}
