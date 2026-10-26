package com.example.cashier.infrastructures.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.example.cashier.applications.security.AuthenticationTokenManager;
import com.example.cashier.commons.properties.SecretProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenManager implements AuthenticationTokenManager {
    private final SecretProperties secretConfig;

    public JwtTokenManager(SecretProperties secretConfig) {
        this.secretConfig = secretConfig;
    }

    @Override
    public Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretConfig.getJwtSecret().getBytes());
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    @Override
    public Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    @Override
    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + secretConfig.getAccessTokenExpiredDuration()))
                .signWith(getSignInKey())
                .claim("role", role)
                .compact();
    }

    @Override
    public String generateRefreshToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + secretConfig.getRefreshTokenExpiredDuration()))
                .signWith(getSignInKey())
                .claim("role", role)
                .compact();
    }
}
