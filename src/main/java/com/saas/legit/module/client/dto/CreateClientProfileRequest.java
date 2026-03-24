package com.saas.legit.module.client.dto;

public record CreateClientProfileRequest(
        String companyName,
        String billingAddress
) {}
