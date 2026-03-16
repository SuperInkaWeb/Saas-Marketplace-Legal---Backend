package com.saas.legit.module.identity.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String roleName) {
        super("Se produjo un error de configuración en el sistema. Por favor, contacta al soporte técnico.");
    }
}