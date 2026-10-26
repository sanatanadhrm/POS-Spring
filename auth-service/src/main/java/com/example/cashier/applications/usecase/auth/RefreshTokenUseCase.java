package com.example.cashier.applications.usecase.auth;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.request.auth.RefreshTokenRequest;
import com.example.cashier.applications.dto.response.auth.RefreshTokenResponse;
import com.example.cashier.applications.security.AuthenticationTokenManager;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.domains.auth.AuthRepository;
import com.example.cashier.domains.auth.entity.Authentication;

@Service
public class RefreshTokenUseCase {
    private final AuthRepository authRepository;
    private final AuthenticationTokenManager tokenManager;

    public RefreshTokenUseCase(AuthRepository authRepository, AuthenticationTokenManager tokenManager) {
        this.authRepository = authRepository;
        this.tokenManager = tokenManager;
    }

    public RefreshTokenResponse execute(RefreshTokenRequest payload) {
        Authentication auth = this.authRepository.findByToken(payload.getRefreshToken());
        if (auth == null) {
            throw new UnauthorizedException("Invalid Refresh Token");
        }

        if (tokenManager.isTokenExpired(auth.getToken())) {
            throw new UnauthorizedException("Expired Refresh Token");
        }

        String username = tokenManager.extractUsername(auth.getToken());
        String role = tokenManager.extractRole(payload.getRefreshToken());

        String accessToken = tokenManager.generateAccessToken(username, role);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .build();

    }
}
