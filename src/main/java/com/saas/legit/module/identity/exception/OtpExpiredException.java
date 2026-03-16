package com.saas.legit.module.identity.exception;

public class OtpExpiredException extends RuntimeException {

    public OtpExpiredException() {
        super("El código OTP ha expirado. Por favor, solicita uno nuevo.");
    }
}
