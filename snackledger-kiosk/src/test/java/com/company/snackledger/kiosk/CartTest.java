package com.company.snackledger.kiosk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.company.snackledger.kiosk.api.Dto.ItemDto;
import com.company.snackledger.kiosk.ui.Cart;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CartTest {
    @Test
    void quantitiesAndRequestIdReuseUntilReset() {
        var cart = new Cart();
        var requestId = cart.requestId();

        cart.add(new ItemDto(1L, "Cola", "100", new BigDecimal("1.25"), true));
        cart.add(new ItemDto(1L, "Cola", "100", new BigDecimal("1.25"), true));

        assertEquals(new BigDecimal("2.50"), cart.total());
        assertEquals(requestId, cart.requestId());

        cart.clearForNewPurchase();

        assertNotEquals(requestId, cart.requestId());
    }
}
