package com.saas.legit.module.marketplace.exception;

public class DuplicateProposalException extends RuntimeException {
    public DuplicateProposalException() {
        super("Ya has enviado una propuesta para este caso");
    }
}
