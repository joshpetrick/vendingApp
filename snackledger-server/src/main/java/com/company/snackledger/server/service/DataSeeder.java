package com.company.snackledger.server.service;

import com.company.snackledger.server.config.DemoUsers;
import com.company.snackledger.server.model.AppUser;
import com.company.snackledger.server.model.Item;
import com.company.snackledger.server.model.KioskDevice;
import com.company.snackledger.server.repo.AppUserRepository;
import com.company.snackledger.server.repo.ItemRepository;
import com.company.snackledger.server.repo.KioskDeviceRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private static final BigDecimal QUARTER = new BigDecimal("0.25");

    private final AppUserRepository users;
    private final ItemRepository items;
    private final KioskDeviceRepository kiosks;
    private final ApiKeyService keys;
    private final boolean createTestUsers;
    private final boolean createDemoSnacks;

    DataSeeder(
            AppUserRepository users,
            ItemRepository items,
            KioskDeviceRepository kiosks,
            ApiKeyService keys,
            @Value("${snackledger.seed.test-users:true}") boolean createTestUsers,
            @Value("${snackledger.seed.demo-snacks:true}") boolean createDemoSnacks) {
        this.users = users;
        this.items = items;
        this.kiosks = kiosks;
        this.keys = keys;
        this.createTestUsers = createTestUsers;
        this.createDemoSnacks = createDemoSnacks;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedItems();
        seedKioskDevice();
    }

    private void seedUsers() {
        if (users.count() > 0) {
            return;
        }

        if (!createTestUsers) {
            var user = new AppUser();
            user.username = "john";
            user.displayName = "John Smith";
            user.badgeId = "BADGE100";
            user.balance = new BigDecimal("10.00");
            users.save(user);
            return;
        }

        for (int index = 0; index < DemoUsers.NAMES.size(); index++) {
            String name = DemoUsers.NAMES.get(index);
            var user = new AppUser();
            user.username = name;
            user.displayName = name;
            user.badgeId = "BADGE" + String.format("%03d", index + 1);
            user.balance = testBalance(index);
            users.save(user);
        }
    }

    private BigDecimal testBalance(int index) {
        int quarterSteps = ((index * 17) % 81) - 40;
        return QUARTER.multiply(BigDecimal.valueOf(quarterSteps));
    }

    private void seedItems() {
        if (items.count() > 0) {
            return;
        }

        if (!createDemoSnacks) {
            saveItem("Coca-Cola Can", "100000000001", "0.75", "/images/snacks/soda.svg");
            return;
        }

        seedSodas();
        seedEnergyDrinks();
        seedChips();
        seedGranolaBars();
        seedPreparedFoods();
    }

    private void seedSodas() {
        saveItem("Coca-Cola", "SODA0001", "0.75", "/images/snacks/soda.svg");
        saveItem("Diet Coke", "SODA0002", "0.75", "/images/snacks/soda.svg");
        saveItem("Pepsi", "SODA0003", "0.75", "/images/snacks/soda.svg");
        saveItem("Diet Pepsi", "SODA0004", "0.75", "/images/snacks/soda.svg");
        saveItem("Dr Pepper", "SODA0005", "0.75", "/images/snacks/soda.svg");
        saveItem("Sprite", "SODA0006", "0.75", "/images/snacks/soda.svg");
        saveItem("Mountain Dew", "SODA0007", "0.75", "/images/snacks/soda.svg");
        saveItem("Root Beer", "SODA0008", "0.75", "/images/snacks/soda.svg");
    }

    private void seedEnergyDrinks() {
        saveItem("Red Bull", "ENERGY001", "2.25", "/images/snacks/energy.svg");
        saveItem("Monster", "ENERGY002", "2.25", "/images/snacks/energy.svg");
        saveItem("Rockstar", "ENERGY003", "2.25", "/images/snacks/energy.svg");
        saveItem("Celsius", "ENERGY004", "2.25", "/images/snacks/energy.svg");
        saveItem("Bang", "ENERGY005", "2.25", "/images/snacks/energy.svg");
    }

    private void seedChips() {
        saveItem("Doritos Nacho Cheese", "CHIP0001", "0.75", "/images/snacks/chips.svg");
        saveItem("Doritos Cool Ranch", "CHIP0002", "0.75", "/images/snacks/chips.svg");
        saveItem("Lay's Classic", "CHIP0003", "0.75", "/images/snacks/chips.svg");
        saveItem("Lay's BBQ", "CHIP0004", "0.75", "/images/snacks/chips.svg");
        saveItem("Cheetos", "CHIP0005", "0.75", "/images/snacks/chips.svg");
        saveItem("Fritos", "CHIP0006", "0.75", "/images/snacks/chips.svg");
        saveItem("Ruffles Cheddar", "CHIP0007", "0.75", "/images/snacks/chips.svg");
        saveItem("Sun Chips", "CHIP0008", "0.75", "/images/snacks/chips.svg");
    }

    private void seedGranolaBars() {
        saveItem("Nature Valley Oats & Honey", "BAR0001", "0.75", "/images/snacks/granola.svg");
        saveItem("Nature Valley Peanut Butter", "BAR0002", "0.75", "/images/snacks/granola.svg");
        saveItem("Quaker Chocolate Chip", "BAR0003", "0.75", "/images/snacks/granola.svg");
        saveItem("Quaker Peanut Butter", "BAR0004", "0.75", "/images/snacks/granola.svg");
        saveItem("Kind Dark Chocolate Nuts", "BAR0005", "0.75", "/images/snacks/granola.svg");
        saveItem("Clif Chocolate Chip", "BAR0006", "0.75", "/images/snacks/granola.svg");
        saveItem("Nutri-Grain Strawberry", "BAR0007", "0.75", "/images/snacks/granola.svg");
        saveItem("Fiber One Chocolate", "BAR0008", "0.75", "/images/snacks/granola.svg");
    }

    private void seedPreparedFoods() {
        saveItem("Sausage Breakfast Sandwich", "FOOD0001", "0.75", "/images/snacks/sandwich.svg");
        saveItem("Bacon Breakfast Sandwich", "FOOD0002", "0.75", "/images/snacks/sandwich.svg");
        saveItem("Egg & Cheese Breakfast Sandwich", "FOOD0003", "0.75", "/images/snacks/sandwich.svg");
        saveItem("Pizza Rolls", "FOOD0004", "0.75", "/images/snacks/pizza-rolls.svg");
        saveItem("Pepperoni Hot Pocket", "FOOD0005", "0.75", "/images/snacks/sandwich.svg");
        saveItem("Ham & Cheese Hot Pocket", "FOOD0006", "0.75", "/images/snacks/sandwich.svg");
    }

    private void saveItem(String name, String barcode, String price, String imageUrl) {
        var item = new Item();
        item.name = name;
        item.barcode = barcode;
        item.price = new BigDecimal(price);
        item.imageUrl = imageUrl;
        items.save(item);
    }

    private void seedKioskDevice() {
        if (kiosks.count() == 0) {
            var kiosk = new KioskDevice();
            kiosk.kioskName = "Main Office Kiosk";
            kiosk.kioskIdentifier = "MAIN-OFFICE-KIOSK";
            kiosk.apiKeyHash = keys.hash("dev-kiosk-key-change-me");
            kiosks.save(kiosk);
        }
    }
}
