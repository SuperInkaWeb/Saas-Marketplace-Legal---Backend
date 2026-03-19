package com.saas.legit.module.marketplace.dto;

public record ScheduleResponse(
        Long id,
        Integer dayOfWeek,
        String startTime,
        String endTime,
        Boolean isActive
) {}
