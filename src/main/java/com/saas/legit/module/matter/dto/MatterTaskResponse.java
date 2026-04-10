package com.saas.legit.module.matter.dto;

import com.saas.legit.module.matter.model.MatterTaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MatterTaskResponse {
    private UUID publicId;
    private String title;
    private String description;
    private OffsetDateTime dueDate;
    private OffsetDateTime completedAt;
    private MatterTaskStatus status;
    private OffsetDateTime createdAt;
}
