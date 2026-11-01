package com.example.cashier.infrastructures.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;
import com.example.cashier.applications.dto.notification.NotificationPayload;
import com.example.cashier.applications.dto.notification.NotificationType;
import com.example.cashier.applications.dto.notification.receipt.ReceiptData;
import com.example.cashier.applications.port.output.ReceiptGeneratorPort;
import com.example.cashier.applications.service.NotificationDispatcher;
import com.example.cashier.infrastructures.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Event Listener (Consumer) yang berjalan di latar belakang secara
 * Asynchronous.
 * Mendengarkan dan memproses setiap pesan yang masuk ke antrean
 * "order.created.queue".
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    /**
     * Anotasi @RabbitListener bertindak sebagai "telinga".
     * Begitu ada pesan JSON masuk ke antrean "order.created.queue",
     * Spring AMQP secara otomatis mem-parsing JSON tersebut menjadi objek
     * OrderCreatedEvent
     * dan mengeksekusi method ini di thread latar belakang (non-blocking).
     */

    private final NotificationDispatcher notificationDispatcher;
    private final ReceiptGeneratorPort receiptGeneratorPort;

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        try {

            ReceiptData receiptData = ReceiptData.builder()
                    .orderId(event.getOrderId())
                    .storeName("Warung Babeh")
                    .totalPrice(event.getTotalPrice())
                    .paymentMethod(event.getPaymentMethod())
                    .transactionDate(event.getCreatedAt())
                    .build();
            byte[] pdfBytes = receiptGeneratorPort.generateReceipt(receiptData);

            NotificationPayload<ReceiptData> payload = NotificationPayload.<ReceiptData>builder()
                    .recipientName("Pelanggan Kasir")
                    .phoneNumber("08123456789") // Hardcode simulasi
                    .recipientEmail("zantanad.24@gmail.com")
                    .type(NotificationType.RECEIPT)
                    .subject("Struk Pembayaran #" + event.getOrderId())
                    .data(receiptData) // <-- DTO struk masuk ke sini!
                    .attachment(pdfBytes) // File PDF struk
                    .attachmentFileName("Struk-" + event.getOrderId() + ".pdf")
                    .build();
            notificationDispatcher.dispatch(payload);

            log.info("[RABBITMQ LISTENER] Berhasil memproses event untuk Order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("[RABBITMQ LISTENER] Gagal memproses event untuk Order ID: {}. Alasan: {}",
                    event.getOrderId(), e.getMessage(), e);
            // Catatan: Jika terjadi error di sini, RabbitMQ secara default bisa me-retry
            // atau memindahkan pesan ke Dead Letter Queue (DLQ) agar tidak hilang.
        }
    }

}
