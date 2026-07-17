package com.company.snackledger.server.api;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class KioskDtos {
    public record Health(String status, OffsetDateTime serverTimestamp, String apiVersion, String applicationVersion) {}

    public record KioskConfig(String apiBasePath, int heartbeatSeconds) {}

    public record UserDto(Long id, String displayName, BigDecimal balance, boolean active) {}

    public record ItemDto(Long id, String name, String barcode, BigDecimal price, boolean active) {}

    public record PurchaseItem(Long itemId, int quantity) {}

    public record PurchaseRequest(String requestId, String kioskId, Long userId, List<PurchaseItem> items) {}

    public record PurchaseResponse(
            Long purchaseId,
            String requestId,
            Long userId,
            BigDecimal previousBalance,
            BigDecimal purchaseTotal,
            BigDecimal newBalance,
            OffsetDateTime completedAt) {}
}
