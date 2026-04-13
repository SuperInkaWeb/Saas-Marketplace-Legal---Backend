package com.saas.legit.module.matter.dto;

import com.saas.legit.module.matter.model.MatterStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MatterResponse {
    private UUID publicId;
    private String title;
    private String description;
    private String number;
    private MatterStatus status;
    private String jurisdiction;
    private OffsetDateTime startDate;
    private OffsetDateTime estimatedEndDate;
    private String clientName;
    private Long clientId;
}
