package com.example.cashier.infrastructures.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cashier.domains.user.entity.User;

public interface SpringDataUserJpaRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

}
