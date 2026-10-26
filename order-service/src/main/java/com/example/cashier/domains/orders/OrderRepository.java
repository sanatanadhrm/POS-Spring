package com.example.cashier.domains.orders;

import java.util.Optional;

import com.example.cashier.domains.orders.entity.Orders;

public interface OrderRepository {
    Orders save(Orders orders);
    Optional<Orders> findByIdempotencyKey(String idempotencyKey);
}
