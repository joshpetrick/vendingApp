package com.company.snackledger.server.admin;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record AdminOverviewDto(
        OffsetDateTime currentTime,
        OffsetDateTime lastRefreshed,
        Summary summary,
        List<UserBalanceSummary> userPreview,
        List<SnackSummary> snackPreview,
        FinancialSummary financialSummary,
        List<KioskStatusSummary> kiosks,
        List<RecentActivity> recentActivity,
        List<AdminAlert> alerts) {

    public record Summary(
            long activeUsers,
            long inactiveUsers,
            BigDecimal totalAmountOwed,
            long usersOwingMoney,
            BigDecimal totalUserCredit,
            long usersWithCredit,
            long activeSnacks,
            long inactiveSnacks,
            long onlineKiosks,
            long totalKiosks,
            long kiosksRequiringAttention,
            long purchasesToday,
            BigDecimal purchaseValueToday) {}

    public record UserBalanceSummary(
            Long id,
            String displayName,
            boolean active,
            BigDecimal balance,
            String balanceStatus,
            String daysNegative,
            String lastActivity) {}

    public record SnackSummary(
            Long id,
            String name,
            String category,
            BigDecimal price,
            String barcode,
            String status,
            String lastUpdated) {}

    public record FinancialSummary(
            BigDecimal totalAmountOwed,
            long usersOwingMoney,
            BigDecimal averageDebt,
            String oldestOutstandingNegativeBalance,
            BigDecimal depositsThisWeek,
            BigDecimal lateFeesThisWeek,
            long reminderEmailsThisWeek,
            List<AccountAttention> accountsRequiringAttention) {}

    public record AccountAttention(
            Long userId,
            String displayName,
            BigDecimal amountOwed,
            String daysNegative,
            String reminderStatus,
            String lastReminder) {}

    public record KioskStatusSummary(
            Long id,
            String name,
            String identifier,
            String status,
            String lastHeartbeat,
            String applicationVersion,
            long purchasesToday,
            String mostRecentPurchase,
            String authenticationStatus) {}

    public record RecentActivity(
            String icon,
            String timestamp,
            String actor,
            String description,
            BigDecimal amount,
            String link) {}

    public record AdminAlert(String severity, String title, String explanation, String recommendedAction, String link) {}
}
