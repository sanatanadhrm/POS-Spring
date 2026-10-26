package com.example.cashier.infrastructures.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cashier.domains.auth.entity.Authentication;

public interface SpringDataAuthJpaRepository extends JpaRepository<Authentication, String> {

    Optional<Authentication> findByToken(String token);
}
