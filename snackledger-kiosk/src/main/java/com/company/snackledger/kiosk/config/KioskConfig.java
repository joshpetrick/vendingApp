package com.company.snackledger.kiosk.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

public record KioskConfig(
        String serverUrl,
        String kioskId,
        String apiKey,
        boolean fullscreen,
        int resetSeconds,
        int connectionTimeoutSeconds,
        int requestTimeoutSeconds) {

    public static KioskConfig load() throws IOException {
        var properties = new Properties();
        Path path = Paths.get(System.getenv().getOrDefault("SNACKLEDGER_KIOSK_CONFIG", "kiosk.properties"));
        if (Files.exists(path)) {
            try (var input = Files.newInputStream(path)) {
                properties.load(input);
            }
        }

        return new KioskConfig(
                value("SERVER_URL", properties, "server.url", "http://localhost:8080"),
                value("KIOSK_ID", properties, "kiosk.id", "MAIN-OFFICE-KIOSK"),
                value("SNACKLEDGER_KIOSK_API_KEY", properties, "kiosk.api-key", ""),
                Boolean.parseBoolean(value("KIOSK_FULLSCREEN", properties, "kiosk.fullscreen", "false")),
                Integer.parseInt(value("KIOSK_RESET_SECONDS", properties, "kiosk.reset-seconds", "8")),
                Integer.parseInt(value("KIOSK_CONNECTION_TIMEOUT_SECONDS", properties, "kiosk.connection-timeout-seconds", "5")),
                Integer.parseInt(value("KIOSK_REQUEST_TIMEOUT_SECONDS", properties, "kiosk.request-timeout-seconds", "15")));
    }

    static String value(String environmentVariable, Properties properties, String propertyName, String defaultValue) {
        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        String propertyValue = properties.getProperty(propertyName, defaultValue);
        if (propertyValue.startsWith("${") && propertyValue.endsWith("}")) {
            return System.getenv().getOrDefault(propertyValue.substring(2, propertyValue.length() - 1), "");
        }
        return propertyValue;
    }

    public Duration connectTimeout() {
        return Duration.ofSeconds(connectionTimeoutSeconds);
    }

    public Duration requestTimeout() {
        return Duration.ofSeconds(requestTimeoutSeconds);
    }
}
