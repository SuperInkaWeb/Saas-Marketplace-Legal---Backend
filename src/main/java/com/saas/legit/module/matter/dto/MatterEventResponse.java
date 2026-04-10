package com.saas.legit.module.matter.dto;

import com.saas.legit.module.matter.model.MatterEventType;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MatterEventResponse {
    private UUID publicId;
    private String title;
    private String description;
    private MatterEventType eventType;
    private OffsetDateTime eventDate;
    private OffsetDateTime createdAt;
    private UUID documentPublicId;
    private String documentName;
}
