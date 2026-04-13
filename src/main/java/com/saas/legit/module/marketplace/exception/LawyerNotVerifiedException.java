package com.saas.legit.module.marketplace.exception;

public class LawyerNotVerifiedException extends RuntimeException {
    public LawyerNotVerifiedException() {
        super("Solo los abogados verificados pueden realizar esta acción");
    }
}
