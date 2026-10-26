package com.example.cashier.infrastructures.messaging;

import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;
import com.example.cashier.applications.gateway.OrderEventPublisher;
import com.example.cashier.infrastructures.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adapter (Implementasi Infrastruktur) untuk OrderEventPublisher menggunakan RabbitMQ.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQOrderEventPublisher implements OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("[RABBITMQ PRODUCER] Mengirim event {} untuk Order ID: {}", 
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY, event.getOrderId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                event,
                message -> {
                    // Teruskan X-Correlation-ID jika ada di MDC agar tracing log tetap terhubung
                    String correlationId = MDC.get("correlationId");
                    if (correlationId != null) {
                        message.getMessageProperties().setHeader("X-Correlation-ID", correlationId);
                    }
                    return message;
                }
        );
    }
}
