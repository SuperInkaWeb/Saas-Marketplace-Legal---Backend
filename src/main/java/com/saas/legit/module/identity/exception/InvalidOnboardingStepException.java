package com.saas.legit.module.identity.exception;

public class InvalidOnboardingStepException extends RuntimeException {

    public InvalidOnboardingStepException(String currentStep, String requiredStep) {
        super("Paso de onboarding inválido. Estado actual: " + currentStep + ". Se requiere: " + requiredStep);
    }
}
