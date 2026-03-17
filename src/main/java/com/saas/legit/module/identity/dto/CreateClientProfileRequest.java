package com.saas.legit.module.identity.dto;

public record CreateClientProfileRequest(
        String companyName,
        String billingAddress
) {}
