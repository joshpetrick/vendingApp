package com.company.snackledger.server.api;

import com.company.snackledger.server.api.KioskDtos.Health;
import com.company.snackledger.server.api.KioskDtos.ItemDto;
import com.company.snackledger.server.api.KioskDtos.KioskConfig;
import com.company.snackledger.server.api.KioskDtos.PurchaseRequest;
import com.company.snackledger.server.api.KioskDtos.PurchaseResponse;
import com.company.snackledger.server.api.KioskDtos.UserDto;
import com.company.snackledger.server.model.KioskDevice;
import com.company.snackledger.server.model.LedgerTransaction;
import com.company.snackledger.server.model.Purchase;
import com.company.snackledger.server.repo.AppUserRepository;
import com.company.snackledger.server.repo.ItemRepository;
import com.company.snackledger.server.repo.KioskDeviceRepository;
import com.company.snackledger.server.repo.LedgerTransactionRepository;
import com.company.snackledger.server.repo.PurchaseRepository;
import com.company.snackledger.server.service.ApiKeyService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/kiosk")
public class KioskApiController {
    private final AppUserRepository users;
    private final ItemRepository items;
    private final KioskDeviceRepository kiosks;
    private final PurchaseRepository purchases;
    private final LedgerTransactionRepository ledger;
    private final ApiKeyService keys;

    public KioskApiController(
            AppUserRepository users,
            ItemRepository items,
            KioskDeviceRepository kiosks,
            PurchaseRepository purchases,
            LedgerTransactionRepository ledger,
            ApiKeyService keys) {
        this.users = users;
        this.items = items;
        this.kiosks = kiosks;
        this.purchases = purchases;
        this.ledger = ledger;
        this.keys = keys;
    }

    @GetMapping("/health")
    Health health() {
        return new Health("UP", OffsetDateTime.now(), "v1", "0.1.0");
    }

    @GetMapping("/config")
    KioskConfig config() {
        return new KioskConfig("/api/v1/kiosk", 60);
    }

    @GetMapping("/users/badge/{badgeId}")
    UserDto badge(@PathVariable("badgeId") String badgeId, HttpServletRequest request) {
        requireKiosk(request);
        var user = users.findByBadgeId(badgeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserDto(user.id, user.displayName, user.balance, user.active);
    }

    @GetMapping("/users/search")
    List<UserDto> userSearch(@RequestParam("query") String query, HttpServletRequest request) {
        requireKiosk(request);
        return users.findTop10ByDisplayNameContainingIgnoreCase(query).stream()
                .map(user -> new UserDto(user.id, user.displayName, user.balance, user.active))
                .toList();
    }

    @GetMapping("/items/barcode/{barcode}")
    ItemDto barcode(@PathVariable("barcode") String barcode, HttpServletRequest request) {
        requireKiosk(request);
        var item = items.findByBarcode(barcode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        return new ItemDto(item.id, item.name, item.barcode, item.price, item.active, item.imageUrl);
    }

    @GetMapping("/items/search")
    List<ItemDto> itemSearch(@RequestParam("query") String query, HttpServletRequest request) {
        requireKiosk(request);
        return items.findTop10ByNameContainingIgnoreCase(query).stream()
                .map(item -> new ItemDto(item.id, item.name, item.barcode, item.price, item.active, item.imageUrl))
                .toList();
    }

    @PostMapping("/purchases")
    @Transactional
    PurchaseResponse purchase(@RequestBody PurchaseRequest request, HttpServletRequest httpRequest) {
        requireKiosk(httpRequest);
        return purchases.findByKioskIdAndRequestId(request.kioskId(), request.requestId())
                .map(this::toResponse)
                .orElseGet(() -> create(request));
    }

    private PurchaseResponse create(PurchaseRequest request) {
        var user = users.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!user.active) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User inactive");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (var line : request.items()) {
            if (line.quantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quantity");
            }
            var item = items.findById(line.itemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
            if (!item.active) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Item inactive");
            }
            total = total.add(item.price.multiply(BigDecimal.valueOf(line.quantity())));
        }

        var purchase = new Purchase();
        purchase.kioskId = request.kioskId();
        purchase.requestId = request.requestId();
        purchase.userId = user.id;
        purchase.previousBalance = user.balance;
        purchase.purchaseTotal = total;
        purchase.newBalance = user.balance.subtract(total);
        purchase.completedAt = OffsetDateTime.now();

        user.balance = purchase.newBalance;
        users.save(user);
        purchases.save(purchase);

        var ledgerTransaction = new LedgerTransaction();
        ledgerTransaction.userId = user.id;
        ledgerTransaction.type = "PURCHASE";
        ledgerTransaction.amount = total.negate();
        ledgerTransaction.balanceAfter = user.balance;
        ledgerTransaction.reference = "kiosk:" + request.kioskId() + ":" + request.requestId();
        ledger.save(ledgerTransaction);

        return toResponse(purchase);
    }

    private PurchaseResponse toResponse(Purchase purchase) {
        return new PurchaseResponse(
                purchase.id,
                purchase.requestId,
                purchase.userId,
                purchase.previousBalance,
                purchase.purchaseTotal,
                purchase.newBalance,
                purchase.completedAt);
    }

    private KioskDevice requireKiosk(HttpServletRequest request) {
        String kioskId = request.getHeader("X-Kiosk-Id");
        String apiKey = request.getHeader("X-Kiosk-Api-Key");
        var kiosk = kiosks.findByKioskIdentifier(kioskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown kiosk"));
        if (!kiosk.active) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Kiosk disabled");
        }
        if (!keys.matches(apiKey, kiosk.apiKeyHash)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid kiosk API key");
        }
        kiosk.lastConnectionAt = OffsetDateTime.now();
        kiosk.lastKnownIpAddress = request.getRemoteAddr();
        kiosks.save(kiosk);
        return kiosk;
    }
}
