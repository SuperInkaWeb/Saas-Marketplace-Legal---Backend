package com.saas.legit.module.marketplace.dto;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardStatsResponse(
        int pendingAppointments,
        int totalProposals,
        BigDecimal ratingAvg,
        int reviewCount,
        Map<Integer, Long> ratingBreakdown
) {}
