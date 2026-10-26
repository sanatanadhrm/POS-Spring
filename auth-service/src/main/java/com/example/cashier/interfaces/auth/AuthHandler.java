package com.example.cashier.interfaces.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cashier.applications.dto.request.auth.LogOutRequest;
import com.example.cashier.applications.dto.request.auth.LoginRequest;
import com.example.cashier.applications.dto.request.auth.RefreshTokenRequest;
import com.example.cashier.applications.dto.response.auth.LoginResponse;
import com.example.cashier.applications.dto.response.auth.RefreshTokenResponse;
import com.example.cashier.applications.usecase.auth.LogOutUseCase;
import com.example.cashier.applications.usecase.auth.LoginUseCase;
import com.example.cashier.applications.usecase.auth.RefreshTokenUseCase;
import com.example.cashier.commons.dto.WebResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j

public class AuthHandler {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogOutUseCase logOutUseCase;

    public AuthHandler(LoginUseCase loginUseCase, RefreshTokenUseCase refreshTokenUseCase,
            LogOutUseCase logOutUseCase) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logOutUseCase = logOutUseCase;
    }

    @PostMapping("/login")
    public WebResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginResponse response = this.loginUseCase.execute(request);

        return WebResponse.<LoginResponse>builder()
                .status("Success")
                .message("Login successfully")
                .data(response)
                .build();
    }

    @PutMapping("/refresh")
    public WebResponse<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        RefreshTokenResponse response = this.refreshTokenUseCase.execute(request);

        return WebResponse.<RefreshTokenResponse>builder()
                .status("Success")
                .message("Refresh Token successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/logout")
    public WebResponse<String> logout(@Valid @RequestBody LogOutRequest request) {
        this.logOutUseCase.execute(request);
        return WebResponse.<String>builder()
                .status("Success")
                .message("Logout successfully")
                .data(null)
                .build();

    }

}
