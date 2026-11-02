package com.example.cashier.applications.usecase.order;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;
import com.example.cashier.applications.dto.notification.NotificationPayload;
import com.example.cashier.applications.dto.notification.NotificationType;
import com.example.cashier.applications.port.output.ReceiptGeneratorPort;
import com.example.cashier.applications.service.NotificationDispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventUseCase {
    private final NotificationDispatcher notificationDispatcher;
    private final ReceiptGeneratorPort receiptGeneratorPort;

    public void execute(OrderCreatedEvent payload) {

        byte[] pdfBytes = receiptGeneratorPort.generateReceipt(payload);

        NotificationPayload<OrderCreatedEvent> notificationPayload = NotificationPayload
                .<OrderCreatedEvent>builder()
                .recipientName("Pelanggan Kasir")
                .phoneNumber("08123456789") // Hardcode simulasi
                .recipientEmail("zantanad.24@gmail.com")
                .type(NotificationType.RECEIPT)
                .subject("Struk Pembayaran #" + payload.getOrderId())
                .data(payload) // <-- DTO struk masuk ke sini!
                .attachment(pdfBytes) // File PDF struk
                .attachmentFileName("Struk-" + payload.getOrderId() + ".pdf")
                .build();
        notificationDispatcher.dispatch(notificationPayload);

        log.info("[RABBITMQ LISTENER] Berhasil memproses event untuk Order ID: {}", payload.getOrderId());

    }
}
