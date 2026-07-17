package com.company.snackledger.server.service;

import com.company.snackledger.server.model.AppUser;
import com.company.snackledger.server.model.Item;
import com.company.snackledger.server.model.KioskDevice;
import com.company.snackledger.server.repo.AppUserRepository;
import com.company.snackledger.server.repo.ItemRepository;
import com.company.snackledger.server.repo.KioskDeviceRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final AppUserRepository users;
    private final ItemRepository items;
    private final KioskDeviceRepository kiosks;
    private final ApiKeyService keys;

    DataSeeder(AppUserRepository users, ItemRepository items, KioskDeviceRepository kiosks, ApiKeyService keys) {
        this.users = users;
        this.items = items;
        this.kiosks = kiosks;
        this.keys = keys;
    }

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            var user = new AppUser();
            user.displayName = "John Smith";
            user.badgeId = "BADGE100";
            user.balance = new BigDecimal("10.00");
            users.save(user);
        }

        if (items.count() == 0) {
            var item = new Item();
            item.name = "Coca-Cola Can";
            item.barcode = "100000000001";
            item.price = new BigDecimal("1.00");
            items.save(item);
        }

        if (kiosks.count() == 0) {
            var kiosk = new KioskDevice();
            kiosk.kioskName = "Main Office Kiosk";
            kiosk.kioskIdentifier = "MAIN-OFFICE-KIOSK";
            kiosk.apiKeyHash = keys.hash("dev-kiosk-key-change-me");
            kiosks.save(kiosk);
        }
    }
}
