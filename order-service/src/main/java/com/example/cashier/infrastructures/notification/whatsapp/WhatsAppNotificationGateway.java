package com.example.cashier.infrastructures.notification.whatsapp;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.notification.NotificationPayload;
import com.example.cashier.applications.port.output.NotificationChannelSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WhatsAppNotificationGateway implements NotificationChannelSender {

    private final Executor notificationExecutor;

    public WhatsAppNotificationGateway(@Qualifier("notificationExecutor") Executor notificationExecutor) {
        this.notificationExecutor = notificationExecutor;
    }

    @Override
    public boolean isApplicable(NotificationPayload<?> payload) {
        return payload.getPhoneNumber() != null && !payload.getPhoneNumber().isBlank();
    }

    @Override
    public CompletableFuture<Void> sendAsync(NotificationPayload<?> payload) {
        return CompletableFuture.runAsync(() -> {
            log.info("[WHATSAPP GATEWAY] Mengirim notifikasi ke {}", payload.getPhoneNumber());
        }, this.notificationExecutor);
    }

}
