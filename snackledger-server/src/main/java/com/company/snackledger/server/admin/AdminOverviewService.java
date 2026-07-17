package com.company.snackledger.server.admin;

import com.company.snackledger.server.admin.AdminOverviewDto.AccountAttention;
import com.company.snackledger.server.admin.AdminOverviewDto.AdminAlert;
import com.company.snackledger.server.admin.AdminOverviewDto.FinancialSummary;
import com.company.snackledger.server.admin.AdminOverviewDto.KioskStatusSummary;
import com.company.snackledger.server.admin.AdminOverviewDto.RecentActivity;
import com.company.snackledger.server.admin.AdminOverviewDto.SnackSummary;
import com.company.snackledger.server.admin.AdminOverviewDto.Summary;
import com.company.snackledger.server.admin.AdminOverviewDto.UserBalanceSummary;
import com.company.snackledger.server.model.AppUser;
import com.company.snackledger.server.model.Item;
import com.company.snackledger.server.model.KioskDevice;
import com.company.snackledger.server.model.Purchase;
import com.company.snackledger.server.repo.AppUserRepository;
import com.company.snackledger.server.repo.ItemRepository;
import com.company.snackledger.server.repo.KioskDeviceRepository;
import com.company.snackledger.server.repo.PurchaseRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdminOverviewService {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final Duration ONLINE_THRESHOLD = Duration.ofSeconds(90);
    private static final Duration DELAYED_THRESHOLD = Duration.ofMinutes(5);

    private final AppUserRepository users;
    private final ItemRepository items;
    private final KioskDeviceRepository kiosks;
    private final PurchaseRepository purchases;

    public AdminOverviewService(
            AppUserRepository users, ItemRepository items, KioskDeviceRepository kiosks, PurchaseRepository purchases) {
        this.users = users;
        this.items = items;
        this.kiosks = kiosks;
        this.purchases = purchases;
    }

    public AdminOverviewDto getOverview() {
        OffsetDateTime now = OffsetDateTime.now();
        List<AppUser> userPreviewSource = users.findTop50ByOrderByActiveDescDisplayNameAsc();
        List<Item> snackPreviewSource = items.findTop50ByOrderByActiveAscNameAsc();
        List<KioskDevice> kioskDevices = kiosks.findAll();
        List<Purchase> recentPurchases = purchases.findTop10ByOrderByCompletedAtDesc();
        OffsetDateTime startOfDay = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());

        long activeUsers = users.countByActiveTrue();
        long inactiveUsers = users.countByActiveFalse();
        BigDecimal totalDebt = positive(users.sumNegativeBalances().orElse(ZERO));
        long usersOwingMoney = users.countByBalanceLessThan(ZERO);
        BigDecimal totalCredit = users.sumPositiveBalances().orElse(ZERO);
        long usersWithCredit = users.countByBalanceGreaterThan(ZERO);
        long activeSnacks = items.countByActiveTrue();
        long inactiveSnacks = items.countByActiveFalse();
        long purchasesToday = purchases.countByCompletedAtGreaterThanEqual(startOfDay);
        BigDecimal purchaseValueToday = purchases.sumPurchaseTotalSince(startOfDay).orElse(ZERO);
        List<KioskStatusSummary> kioskSummaries = kioskDevices.stream().map(kiosk -> toKioskStatus(kiosk, now)).toList();
        long onlineKiosks = kioskSummaries.stream().filter(kiosk -> "Online".equals(kiosk.status())).count();
        long kioskAttention = kioskSummaries.stream().filter(kiosk -> !"Online".equals(kiosk.status())).count();

        var summary = new Summary(
                activeUsers,
                inactiveUsers,
                totalDebt,
                usersOwingMoney,
                totalCredit,
                usersWithCredit,
                activeSnacks,
                inactiveSnacks,
                onlineKiosks,
                kioskDevices.size(),
                kioskAttention,
                purchasesToday,
                purchaseValueToday);

        var financialSummary = new FinancialSummary(
                totalDebt,
                usersOwingMoney,
                usersOwingMoney == 0 ? ZERO : totalDebt.divide(BigDecimal.valueOf(usersOwingMoney), 2, RoundingMode.HALF_UP),
                usersOwingMoney == 0 ? "None" : "Demo data: oldest negative age unavailable",
                ZERO,
                ZERO,
                0,
                userPreviewSource.stream()
                        .filter(user -> user.balance.compareTo(ZERO) < 0)
                        .sorted(Comparator.comparing(user -> user.balance))
                        .limit(6)
                        .map(user -> new AccountAttention(
                                user.id, user.displayName, positive(user.balance), "—", reminderStatus(user), "Never"))
                        .toList());

        return new AdminOverviewDto(
                now,
                now,
                summary,
                userPreviewSource.stream().sorted(this::compareUsers).limit(10).map(this::toUserSummary).toList(),
                snackPreviewSource.stream().sorted(this::compareSnacks).limit(8).map(this::toSnackSummary).toList(),
                financialSummary,
                kioskSummaries,
                toRecentActivity(recentPurchases),
                alerts(userPreviewSource, snackPreviewSource, kioskSummaries));
    }

    private int compareUsers(AppUser left, AppUser right) {
        int leftGroup = userSortGroup(left);
        int rightGroup = userSortGroup(right);
        if (leftGroup != rightGroup) {
            return Integer.compare(leftGroup, rightGroup);
        }
        return left.displayName.compareToIgnoreCase(right.displayName);
    }

    private int userSortGroup(AppUser user) {
        if (!user.active) {
            return 3;
        }
        if (user.balance.compareTo(ZERO) < 0) {
            return 0;
        }
        if (user.balance.compareTo(ZERO) == 0) {
            return 1;
        }
        return 2;
    }

    private UserBalanceSummary toUserSummary(AppUser user) {
        return new UserBalanceSummary(
                user.id,
                user.displayName,
                user.active,
                user.balance,
                balanceStatus(user.balance),
                user.balance.compareTo(ZERO) < 0 ? "—" : "—",
                "No recent activity");
    }

    private int compareSnacks(Item left, Item right) {
        int leftGroup = snackSortGroup(left);
        int rightGroup = snackSortGroup(right);
        if (leftGroup != rightGroup) {
            return Integer.compare(leftGroup, rightGroup);
        }
        return left.name.compareToIgnoreCase(right.name);
    }

    private int snackSortGroup(Item item) {
        if (!item.active || item.barcode == null || item.barcode.isBlank() || item.price == null) {
            return 0;
        }
        return 1;
    }

    private SnackSummary toSnackSummary(Item item) {
        return new SnackSummary(item.id, item.name, category(item), item.price, item.barcode, snackStatus(item), "Seeded data");
    }

    private KioskStatusSummary toKioskStatus(KioskDevice kiosk, OffsetDateTime now) {
        String status;
        if (!kiosk.active) {
            status = "Disabled";
        } else if (kiosk.lastConnectionAt == null) {
            status = "Offline";
        } else {
            Duration age = Duration.between(kiosk.lastConnectionAt, now);
            status = age.compareTo(ONLINE_THRESHOLD) <= 0 ? "Online" : age.compareTo(DELAYED_THRESHOLD) <= 0 ? "Delayed" : "Offline";
        }
        Purchase mostRecent = purchases.findTopByKioskIdOrderByCompletedAtDesc(kiosk.kioskIdentifier).orElse(null);
        return new KioskStatusSummary(
                kiosk.id,
                kiosk.kioskName,
                kiosk.kioskIdentifier,
                status,
                kiosk.lastConnectionAt == null ? "Never" : relative(kiosk.lastConnectionAt, now),
                kiosk.applicationVersion == null ? "Unknown" : kiosk.applicationVersion,
                purchases.countByKioskIdAndCompletedAtGreaterThanEqual(
                        kiosk.kioskIdentifier, now.toLocalDate().atStartOfDay().atOffset(now.getOffset())),
                mostRecent == null ? "No purchases yet" : relative(mostRecent.completedAt, now),
                kiosk.active ? "API key configured" : "Disabled");
    }

    private List<RecentActivity> toRecentActivity(List<Purchase> recentPurchases) {
        if (recentPurchases.isEmpty()) {
            return List.of();
        }
        return recentPurchases.stream()
                .map(purchase -> new RecentActivity(
                        "🧾",
                        relative(purchase.completedAt, OffsetDateTime.now()),
                        "Kiosk",
                        "Purchase completed for user #" + purchase.userId,
                        purchase.purchaseTotal,
                        "/admin/ledger"))
                .toList();
    }

    private List<AdminAlert> alerts(List<AppUser> userPreview, List<Item> snackPreview, List<KioskStatusSummary> kioskSummaries) {
        var alerts = new java.util.ArrayList<AdminAlert>();
        long usersOwing = userPreview.stream().filter(user -> user.balance.compareTo(ZERO) < 0).count();
        if (usersOwing > 0) {
            alerts.add(new AdminAlert("Warning", usersOwing + " users owe money", "Review negative balances and reminders.", "View negative accounts", "/admin/users"));
        }
        long snackIssues = snackPreview.stream().filter(item -> snackSortGroup(item) == 0).count();
        if (snackIssues > 0) {
            alerts.add(new AdminAlert("Warning", snackIssues + " snacks need attention", "Inactive snacks or missing barcode/price information were found.", "Review snacks", "/admin/snacks"));
        }
        long kioskIssues = kioskSummaries.stream().filter(kiosk -> !"Online".equals(kiosk.status())).count();
        if (kioskIssues > 0) {
            alerts.add(new AdminAlert("Critical", kioskIssues + " kiosks require attention", "One or more kiosks are offline, delayed, or disabled.", "Review kiosks", "/admin/kiosks"));
        }
        return alerts;
    }

    private static BigDecimal positive(BigDecimal value) {
        return value.compareTo(ZERO) < 0 ? value.negate() : value;
    }

    private static String balanceStatus(BigDecimal balance) {
        if (balance.compareTo(ZERO) < 0) {
            return "Owes Money";
        }
        if (balance.compareTo(ZERO) > 0) {
            return "Credit Available";
        }
        return "Settled";
    }

    private static String snackStatus(Item item) {
        if (!item.active) {
            return "Inactive";
        }
        if (item.barcode == null || item.barcode.isBlank()) {
            return "Missing Barcode";
        }
        if (item.price == null) {
            return "Missing Price";
        }
        return "Active";
    }

    private static String category(Item item) {
        if (item.barcode == null) {
            return "Uncategorized";
        }
        if (item.barcode.startsWith("SODA")) return "Drinks";
        if (item.barcode.startsWith("ENERGY")) return "Energy Drinks";
        if (item.barcode.startsWith("CHIP")) return "Chips";
        if (item.barcode.startsWith("BAR")) return "Granola Bars";
        if (item.barcode.startsWith("FOOD")) return "Prepared Food";
        return "Snacks";
    }

    private static String reminderStatus(AppUser user) {
        return user.balance.compareTo(ZERO) < 0 ? "Not configured" : "Not needed";
    }

    private static String relative(OffsetDateTime timestamp, OffsetDateTime now) {
        Duration duration = Duration.between(timestamp, now).abs();
        if (duration.toMinutes() < 1) return "Just now";
        if (duration.toHours() < 1) return duration.toMinutes() + " minutes ago";
        if (duration.toDays() < 1) return duration.toHours() + " hours ago";
        return duration.toDays() + " days ago";
    }
}
