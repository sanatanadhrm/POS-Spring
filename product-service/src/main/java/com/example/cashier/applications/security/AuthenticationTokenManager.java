package com.example.cashier.applications.security;

import java.security.Key;

import io.jsonwebtoken.Claims;

public interface AuthenticationTokenManager {

    Key getSignInKey();

    Claims extractAllClaims(String token);

    String extractUsername(String token);

    String extractRole(String token);

    String generateAccessToken(String username, String role);

    String generateRefreshToken(String username, String role);

    Boolean isTokenExpired(String token);

}
