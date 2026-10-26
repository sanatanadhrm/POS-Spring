package com.example.cashier.applications.usecase.auth;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.request.auth.LogOutRequest;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.domains.auth.AuthRepository;
import com.example.cashier.domains.auth.entity.Authentication;

@Service
public class LogOutUseCase {
    private final AuthRepository authRepository;

    public LogOutUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(LogOutRequest payload) {

        Authentication auth = this.authRepository.findByToken(payload.getRefreshToken());

        if (auth == null) {
            throw new UnauthorizedException("Invalid Refresh Token");
        }

        authRepository.delete(auth);

    }
}
