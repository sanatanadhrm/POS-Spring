package com.example.cashier.infrastructures.database.postgresql;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.cashier.domains.orders.OrderRepository;
import com.example.cashier.domains.orders.entity.Orders;
import com.example.cashier.infrastructures.repository.SpringDataOrderJpaRepository;

@Component
public class OrderRepositoryPostgreImpl implements OrderRepository {

    private final SpringDataOrderJpaRepository springDataOrderJpaRepository;

    public OrderRepositoryPostgreImpl(SpringDataOrderJpaRepository springDataOrderJpaRepository) {
        this.springDataOrderJpaRepository = springDataOrderJpaRepository;
    }

    @Override
    public Orders save(Orders orders) {
        return this.springDataOrderJpaRepository.save(orders);
    }

    @Override
    public Optional<Orders> findByIdempotencyKey(String idempotencyKey) {
        return this.springDataOrderJpaRepository.findByIdempotencyKey(idempotencyKey);
    }
}
