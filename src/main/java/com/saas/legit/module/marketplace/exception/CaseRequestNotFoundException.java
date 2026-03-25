package com.saas.legit.module.marketplace.exception;

public class CaseRequestNotFoundException extends RuntimeException {
    public CaseRequestNotFoundException() {
        super("Caso no encontrado");
    }
}