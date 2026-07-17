package com.company.snackledger.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class KioskApiTests {
    private static final String KIOSK_ID = "MAIN-OFFICE-KIOSK";
    private static final String API_KEY = "dev-kiosk-key-change-me";

    @Autowired private MockMvc mvc;

    @Test
    void validApiKeyBadgeBarcodeAndDuplicatePurchase() throws Exception {
        mvc.perform(get("/api/v1/kiosk/users/badge/BADGE100")
                        .header("X-Kiosk-Id", KIOSK_ID)
                        .header("X-Kiosk-Api-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("John Smith"));

        mvc.perform(get("/api/v1/kiosk/items/barcode/100000000001")
                        .header("X-Kiosk-Id", KIOSK_ID)
                        .header("X-Kiosk-Api-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(1.0));

        String body = """
                {
                  "requestId": "r1",
                  "kioskId": "MAIN-OFFICE-KIOSK",
                  "userId": 1,
                  "items": [{"itemId": 1, "quantity": 2}]
                }
                """;

        mvc.perform(post("/api/v1/kiosk/purchases")
                        .header("X-Kiosk-Id", KIOSK_ID)
                        .header("X-Kiosk-Api-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchaseTotal").value(2.0));

        mvc.perform(post("/api/v1/kiosk/purchases")
                        .header("X-Kiosk-Id", KIOSK_ID)
                        .header("X-Kiosk-Api-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchaseTotal").value(2.0));
    }

    @Test
    void invalidApiKeyRejected() throws Exception {
        mvc.perform(get("/api/v1/kiosk/users/badge/BADGE100")
                        .header("X-Kiosk-Id", KIOSK_ID)
                        .header("X-Kiosk-Api-Key", "bad"))
                .andExpect(status().isUnauthorized());
    }
}
