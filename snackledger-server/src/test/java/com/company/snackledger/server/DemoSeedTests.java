package com.company.snackledger.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.snackledger.server.repo.AppUserRepository;
import com.company.snackledger.server.repo.ItemRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoSeedTests {
    @Autowired private AppUserRepository users;
    @Autowired private ItemRepository items;

    @Test
    void createsFiftyDemoUsersWithQuarterIncrementPositiveAndNegativeBalances() {
        var allUsers = users.findAll();

        assertThat(allUsers).hasSize(50);
        assertThat(allUsers).allSatisfy(user -> {
            assertThat(user.username).isEqualTo(user.displayName);
            assertThat(user.balance.remainder(new BigDecimal("0.25"))).isEqualByComparingTo(BigDecimal.ZERO);
        });
        assertThat(allUsers).anySatisfy(user -> assertThat(user.balance).isPositive());
        assertThat(allUsers).anySatisfy(user -> assertThat(user.balance).isNegative());
    }

    @Test
    void createsDemoSnacksWithExpectedPrices() {
        var allItems = items.findAll();

        assertThat(allItems).hasSizeGreaterThanOrEqualTo(30);
        assertThat(items.findByBarcode("SODA0001")).get().extracting(item -> item.price).isEqualTo(new BigDecimal("0.75"));
        assertThat(items.findByBarcode("ENERGY001")).get().extracting(item -> item.price).isEqualTo(new BigDecimal("2.25"));
        assertThat(items.findByBarcode("CHIP0001")).get().extracting(item -> item.price).isEqualTo(new BigDecimal("0.75"));
        assertThat(items.findByBarcode("BAR0001")).get().extracting(item -> item.price).isEqualTo(new BigDecimal("0.75"));
        assertThat(items.findByBarcode("FOOD0004")).get().extracting(item -> item.price).isEqualTo(new BigDecimal("0.75"));
    }
}
