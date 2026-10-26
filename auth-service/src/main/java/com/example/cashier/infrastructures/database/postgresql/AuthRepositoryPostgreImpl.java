package com.example.cashier.infrastructures.database.postgresql;

import org.springframework.stereotype.Component;

import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.domains.auth.AuthRepository;
import com.example.cashier.domains.auth.entity.Authentication;
import com.example.cashier.infrastructures.repository.SpringDataAuthJpaRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthRepositoryPostgreImpl implements AuthRepository {

    private final SpringDataAuthJpaRepository jpaRepository;

    public AuthRepositoryPostgreImpl(SpringDataAuthJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Authentication authentication) {
        this.jpaRepository.save(authentication);
    }

    @Override
    public Authentication findByToken(String token) {
        return this.jpaRepository.findByToken(token).orElse(null);
    }

    @Override
    public void delete(Authentication authentication) {
        try {
            this.jpaRepository.delete(authentication);
        } catch (Exception e) {
            log.error("Failed to delete authentication", e);
            throw new UnauthorizedException("Invalid Refresh Token");
        }
    }
}
