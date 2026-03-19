package com.saas.legit.module.marketplace.exception;

import java.util.List;

public class SpecialtyNotFoundException extends RuntimeException {
    public SpecialtyNotFoundException(List<Long> missingIds) {
        super("Especialidades no encontradas con IDs: " + missingIds);
    }
}
