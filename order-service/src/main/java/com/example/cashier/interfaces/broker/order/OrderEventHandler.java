package com.example.cashier.interfaces.broker.order;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;
import com.example.cashier.applications.usecase.order.OrderCreatedEventUseCase;
import com.example.cashier.infrastructures.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final OrderCreatedEventUseCase orderCreatedEventUseCase;

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("[RABBITMQ LISTENER] Menerima event OrderCreated untuk Order ID: {}", event.getOrderId());
        this.orderCreatedEventUseCase.execute(event);
    }
}
