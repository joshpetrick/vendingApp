package com.company.snackledger.kiosk.api;

import com.company.snackledger.kiosk.api.Dto.Health;
import com.company.snackledger.kiosk.api.Dto.ItemDto;
import com.company.snackledger.kiosk.api.Dto.PurchaseRequest;
import com.company.snackledger.kiosk.api.Dto.PurchaseResponse;
import com.company.snackledger.kiosk.api.Dto.UserDto;
import com.company.snackledger.kiosk.config.KioskConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class KioskApiClient {
    private final KioskConfig config;
    private final HttpClient http;
    private final ObjectMapper json = new ObjectMapper().registerModule(new JavaTimeModule());

    public KioskApiClient(KioskConfig config) {
        this.config = config;
        this.http = HttpClient.newBuilder().connectTimeout(config.connectTimeout()).build();
    }

    public Health health() throws Exception {
        return get("/api/v1/kiosk/health", Health.class);
    }

    public UserDto badge(String badgeId) throws Exception {
        return get("/api/v1/kiosk/users/badge/" + encode(badgeId), UserDto.class);
    }

    public List<UserDto> users(String query) throws Exception {
        return getList("/api/v1/kiosk/users/search?query=" + encode(query), new TypeReference<>() {});
    }

    public ItemDto barcode(String barcode) throws Exception {
        return get("/api/v1/kiosk/items/barcode/" + encode(barcode), ItemDto.class);
    }

    public List<ItemDto> items(String query) throws Exception {
        return getList("/api/v1/kiosk/items/search?query=" + encode(query), new TypeReference<>() {});
    }

    public PurchaseResponse purchase(PurchaseRequest request) throws Exception {
        return send("/api/v1/kiosk/purchases", "POST", json.writeValueAsString(request), PurchaseResponse.class);
    }

    private <T> T get(String path, Class<T> type) throws Exception {
        return send(path, "GET", null, type);
    }

    private <T> T getList(String path, TypeReference<T> type) throws Exception {
        var response = request(path, "GET", null);
        return json.readValue(response.body(), type);
    }

    private <T> T send(String path, String method, String body, Class<T> type) throws Exception {
        var response = request(path, method, body);
        return json.readValue(response.body(), type);
    }

    private HttpResponse<String> request(String path, String method, String body) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create(config.serverUrl() + path))
                .timeout(config.requestTimeout())
                .header("X-Kiosk-Id", config.kioskId())
                .header("X-Kiosk-Api-Key", config.apiKey())
                .header("Content-Type", "application/json");
        if ("POST".equals(method)) {
            builder.POST(HttpRequest.BodyPublishers.ofString(body));
        } else {
            builder.GET();
        }

        var response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new KioskApiException(response.statusCode(), response.body());
        }
        return response;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static class KioskApiException extends RuntimeException {
        private final int status;

        public KioskApiException(int status, String message) {
            super(message);
            this.status = status;
        }

        public int status() {
            return status;
        }
    }
}
