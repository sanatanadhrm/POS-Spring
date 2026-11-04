package com.example.cashier.infrastructures.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class RabbitMQConfig {
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String PRODUCT_RESTORE_QUEUE = "product.restore-stock.queue";
    public static final String ORDER_RESTORE_ROUTING_KEY = "order.restore";

    // 1. Tunjuk Exchange yang sama di RabbitMQ
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    // 2. Buat antrean milik Product Service sendiri
    @Bean
    public Queue productRestoreQueue() {
        return QueueBuilder.durable(PRODUCT_RESTORE_QUEUE).build();
    }

    // 3. Ikat antreannya ke Exchange dengan routing key
    @Bean
    public Binding productRestoreBinding(Queue productRestoreQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(productRestoreQueue)
                .to(orderExchange)
                .with(ORDER_RESTORE_ROUTING_KEY);
    }

    // 4. Message Converter agar otomatis jadi Java Object
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
