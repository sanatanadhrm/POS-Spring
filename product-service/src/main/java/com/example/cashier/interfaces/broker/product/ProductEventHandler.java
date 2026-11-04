package com.example.cashier.interfaces.broker.product;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.event.product.RestoreProductEvent;
import com.example.cashier.applications.usecase.product.RestoreProductEventUseCase;
import com.example.cashier.infrastructures.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventHandler {
    private final RestoreProductEventUseCase restoreProductEventUseCase;

    @RabbitListener(queues = RabbitMQConfig.PRODUCT_RESTORE_QUEUE)
    public void handleRestoreEvent(RestoreProductEvent event) {
        log.info("[RABBITMQ LISTENER] Menerima event OrderCreated untuk Order ID: {}");
        this.restoreProductEventUseCase.execute(event);
    }
}
