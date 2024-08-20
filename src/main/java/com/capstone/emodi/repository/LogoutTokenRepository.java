package com.capstone.emodi.repository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LogoutTokenRepository {
    private final Map<String, Long> logoutTokens = new ConcurrentHashMap<>();

    public void addLogoutToken(String token, Long expirationTime) {
        logoutTokens.put(token, expirationTime);
    }

    public boolean isLoggedOut(String token) {
        return logoutTokens.containsKey(token);
    }

    @Scheduled(fixedRate = 60000)
    public void removeExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        logoutTokens.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }
}