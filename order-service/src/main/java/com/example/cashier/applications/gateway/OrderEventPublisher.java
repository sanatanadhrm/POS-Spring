package com.example.cashier.applications.gateway;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;

/**
 * Port (Interface) di Application Layer untuk mempublikasikan event pesanan.
 * Mengikuti prinsip Clean Architecture / Dependency Inversion:
 * Use case tidak terikat pada teknologi broker tertentu (RabbitMQ, Kafka, SQS, dll).
 */
public interface OrderEventPublisher {

    /**
     * Mempublikasikan event bahwa pesanan baru telah berhasil dibuat.
     *
     * @param event payload data pesanan yang baru dibuat
     */
    void publishOrderCreated(OrderCreatedEvent event);
}
