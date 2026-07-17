package com.company.snackledger.kiosk.ui;

import com.company.snackledger.kiosk.api.Dto.ItemDto;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Cart {
    private final Map<Long, Line> lines = new LinkedHashMap<>();
    private String requestId = UUID.randomUUID().toString();

    public void add(ItemDto item) {
        lines.compute(item.id(), (id, line) -> line == null ? new Line(item, 1) : line.increase());
    }

    public void increase(Long itemId) {
        lines.computeIfPresent(itemId, (id, line) -> line.increase());
    }

    public void decrease(Long itemId) {
        lines.computeIfPresent(itemId, (id, line) -> line.quantity > 1 ? new Line(line.item, line.quantity - 1) : null);
    }

    public void remove(Long itemId) {
        lines.remove(itemId);
    }

    public void clearForNewPurchase() {
        lines.clear();
        requestId = UUID.randomUUID().toString();
    }

    public String requestId() {
        return requestId;
    }

    public BigDecimal total() {
        return lines.values().stream()
                .map(line -> line.item.price().multiply(BigDecimal.valueOf(line.quantity)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Collection<Line> lines() {
        return lines.values();
    }

    public record Line(ItemDto item, int quantity) {
        Line increase() {
            return new Line(item, quantity + 1);
        }
    }
}
