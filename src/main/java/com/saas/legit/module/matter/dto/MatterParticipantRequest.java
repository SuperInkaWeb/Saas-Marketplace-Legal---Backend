package com.saas.legit.module.matter.dto;

import com.saas.legit.module.matter.model.ParticipantRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MatterParticipantRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Role is required")
    private ParticipantRole role;

    private String email;
    private String phone;
    private String firmOrInstitution;
    private String professionalId;
    private String notes;
}
