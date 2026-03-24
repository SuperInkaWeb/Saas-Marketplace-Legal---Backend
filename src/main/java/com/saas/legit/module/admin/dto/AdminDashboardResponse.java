package com.saas.legit.module.admin.dto;

import java.math.BigDecimal;

public record AdminDashboardResponse(
        long totalUsers,
        long totalLawyers,
        long totalClients,
        long pendingVerifications,
        long totalAppointments,
        long totalReviews,
        long recentRegistrations,
        BigDecimal monthlyRevenue
) {}
