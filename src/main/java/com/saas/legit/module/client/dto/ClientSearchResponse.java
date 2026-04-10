package com.saas.legit.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSearchResponse {
    private UUID publicId;
    private String fullName;
    private String email;
    private String companyName;
    private String avatarUrl;
}
