package com.example.cashier.applications.port.output;

import java.util.concurrent.CompletableFuture;

import com.example.cashier.applications.dto.notification.NotificationPayload;

public interface NotificationChannelSender {
    boolean isApplicable(NotificationPayload<?> payload);

    CompletableFuture<Void> sendAsync(NotificationPayload<?> payload);
}