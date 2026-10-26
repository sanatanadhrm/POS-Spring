package com.example.cashier.applications.usecase.auth;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.request.auth.LoginRequest;
import com.example.cashier.applications.dto.response.auth.LoginResponse;
import com.example.cashier.applications.security.AuthenticationChecker;
import com.example.cashier.applications.security.AuthenticationTokenManager;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.domains.auth.AuthRepository;
import com.example.cashier.domains.auth.entity.Authentication;
import com.example.cashier.domains.user.UserRepository;
import com.example.cashier.domains.user.entity.User;

@Service
public class LoginUseCase {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final AuthenticationTokenManager authenticationManager;
    private final AuthenticationChecker authenticationChecker;

    public LoginUseCase(AuthRepository authRepository, UserRepository userRepository,
            AuthenticationTokenManager authenticationManager, AuthenticationChecker authenticationChecker) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.authenticationChecker = authenticationChecker;
    }

    public LoginResponse execute(LoginRequest request) {
        authenticationChecker.verify(request.getUsername(), request.getPassword());
        User user = userRepository.findByUsername(request.getUsername());

        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        String accessToken = authenticationManager.generateAccessToken(request.getUsername(), user.getRole().getName());
        String refreshToken = authenticationManager.generateRefreshToken(request.getUsername(),
                user.getRole().getName());

        Authentication authentication = Authentication.builder()
                .token(refreshToken)
                .build();
        authRepository.save(authentication);

        return new LoginResponse(accessToken, refreshToken);
    }
}
