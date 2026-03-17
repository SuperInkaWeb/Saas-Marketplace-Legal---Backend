package com.saas.legit.module.identity.exception;

public class KycRequiredException extends RuntimeException {

    public KycRequiredException() {
        super("Se requiere verificación de identidad (KYC) para realizar esta acción.");
    }
}
