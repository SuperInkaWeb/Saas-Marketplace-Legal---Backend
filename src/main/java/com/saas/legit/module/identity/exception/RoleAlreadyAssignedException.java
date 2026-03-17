package com.saas.legit.module.identity.exception;

public class RoleAlreadyAssignedException extends RuntimeException {

    public RoleAlreadyAssignedException() {
        super("El usuario ya tiene un rol asignado.");
    }
}
