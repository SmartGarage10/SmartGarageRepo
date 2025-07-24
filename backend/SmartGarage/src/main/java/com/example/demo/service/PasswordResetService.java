package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {

    private final ConcurrentHashMap<String, String> resetTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> tokenExpiration = new ConcurrentHashMap<>();

    private static final long TOKEN_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(30); // 30 min expiration

    public String createResetToken(String email) {
        String token = UUID.randomUUID().toString();
        resetTokens.put(email, token);
        tokenExpiration.put(email, System.currentTimeMillis() + TOKEN_EXPIRATION_TIME);
        return token;
    }

    public boolean isValidToken(String email, String token) {
        String storedToken = resetTokens.get(email);
        Long expirationTime = tokenExpiration.get(email);

        return storedToken != null && storedToken.equals(token) && System.currentTimeMillis() < expirationTime;
    }

    public void invalidateToken(String email) {
        resetTokens.remove(email);
        tokenExpiration.remove(email);
    }
}
