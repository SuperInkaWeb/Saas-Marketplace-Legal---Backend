package com.saas.legit.module.matter.dto;

import com.saas.legit.module.matter.model.ParticipantRole;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MatterParticipantResponse {
    private UUID publicId;
    private String fullName;
    private ParticipantRole role;
    private String email;
    private String phone;
    private String firmOrInstitution;
    private String professionalId;
    private String notes;
    private OffsetDateTime createdAt;
}
