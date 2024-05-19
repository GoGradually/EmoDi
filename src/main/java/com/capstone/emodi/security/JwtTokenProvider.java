package com.capstone.emodi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.access-token-expiration-ms}")
    private long ACCESS_TOKEN_EXPIRATION_MS;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long REFRESH_TOKEN_EXPIRATION_MS;

    public String generateAccessToken(String loginId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String generateRefreshToken(String loginId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String getLoginIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateAccessToken(String token) {
        try {
            token = token.substring(7);
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            // 토큰 유효성 검사 실패
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            // 토큰 유효성 검사 실패
            return false;
        }
    }
    private final Set<String> refreshTokens = new HashSet<>();

    public void storeRefreshToken(String refreshToken) {
        refreshTokens.add(refreshToken);
    }

    public void removeRefreshToken(String refreshToken) {
        refreshTokens.remove(refreshToken);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        return refreshTokens.contains(refreshToken);
    }
}