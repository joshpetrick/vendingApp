package com.company.snackledger.server.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {
    public String hash(String key) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(key.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to hash kiosk API key", exception);
        }
    }

    public boolean matches(String key, String hash) {
        return key != null && hash(key).equals(hash);
    }
}
