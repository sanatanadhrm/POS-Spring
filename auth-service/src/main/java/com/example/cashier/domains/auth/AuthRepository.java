package com.example.cashier.domains.auth;

import com.example.cashier.domains.auth.entity.Authentication;

public interface AuthRepository {
    void save(Authentication authentication);

    void delete(Authentication authentication);

    Authentication findByToken(String token);
}
