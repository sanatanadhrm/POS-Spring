package com.example.cashier.infrastructures.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cashier.domains.orders.entity.Orders;

public interface SpringDataOrderJpaRepository extends JpaRepository<Orders, String> {
    Optional<Orders> findByIdempotencyKey(String idempotencyKey);
}
