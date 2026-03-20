package com.saas.legit.module.marketplace.dto;

import java.math.BigDecimal;

public record DashboardStatsResponse(
        int pendingAppointments,
        int totalProposals,
        BigDecimal ratingAvg,
        int reviewCount
) {}
