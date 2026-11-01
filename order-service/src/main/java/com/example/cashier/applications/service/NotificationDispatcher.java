package com.example.cashier.applications.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.notification.NotificationPayload;
import com.example.cashier.applications.port.output.NotificationChannelSender;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationDispatcher {
    private final List<NotificationChannelSender> channelSenders;

    public NotificationDispatcher(List<NotificationChannelSender> channelSenders) {
        this.channelSenders = channelSenders;
    }

    public void dispatch(NotificationPayload<?> payload) {
        List<CompletableFuture<Void>> tasks = channelSenders.stream()
                .filter(sender -> sender.isApplicable(payload))
                .map(sender -> sender.sendAsync(payload))
                .toList();
        if (tasks.isEmpty()) {
            log.warn("Tidak ada channel notifikasi yang valid untuk Order: {}", payload.getRecipientName());
            return;
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        log.info("Semua notifikasi berhasil dikirim!");
    }
}
