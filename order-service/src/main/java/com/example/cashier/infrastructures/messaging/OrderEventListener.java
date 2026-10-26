package com.example.cashier.infrastructures.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;
import com.example.cashier.infrastructures.config.RabbitMQConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * Event Listener (Consumer) yang berjalan di latar belakang secara Asynchronous.
 * Mendengarkan dan memproses setiap pesan yang masuk ke antrean "order.created.queue".
 */
@Component
@Slf4j
public class OrderEventListener {

    /**
     * Anotasi @RabbitListener bertindak sebagai "telinga".
     * Begitu ada pesan JSON masuk ke antrean "order.created.queue", 
     * Spring AMQP secara otomatis mem-parsing JSON tersebut menjadi objek OrderCreatedEvent
     * dan mengeksekusi method ini di thread latar belakang (non-blocking).
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("==========================================================");
        log.info("[RABBITMQ LISTENER] Menerima pesan pesanan baru!");
        log.info("Order ID       : {}", event.getOrderId());
        log.info("Store ID       : {}", event.getStoreId());
        log.info("Kasir ID       : {}", event.getCashierId());
        log.info("Total Belanja  : Rp {}", event.getTotalPrice());
        log.info("Metode Bayar   : {}", event.getPaymentMethod());
        log.info("==========================================================");

        try {
            // Simulasi pekerjaan berat di latar belakang:
            
            // 1. Menyiapkan dan mengirimkan struk belanja digital via Email / WhatsApp
            sendDigitalReceipt(event);

            // 2. Merekam data ke sistem analitik / audit akuntansi toko
            recordSalesAudit(event);

            log.info("[RABBITMQ LISTENER] Berhasil memproses event untuk Order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("[RABBITMQ LISTENER] Gagal memproses event untuk Order ID: {}. Alasan: {}",
                    event.getOrderId(), e.getMessage(), e);
            // Catatan: Jika terjadi error di sini, RabbitMQ secara default bisa me-retry
            // atau memindahkan pesan ke Dead Letter Queue (DLQ) agar tidak hilang.
        }
    }

    private void sendDigitalReceipt(OrderCreatedEvent event) {
        log.info("--> [WORKER ASINKRON] Membuat PDF struk dan mengirim notifikasi ke pelanggan (Order: {})",
                event.getOrderId());
    }

    private void recordSalesAudit(OrderCreatedEvent event) {
        log.info("--> [WORKER ASINKRON] Mencatat transaksi ke buku besar laporan keuangan toko: {}", 
                event.getStoreId());
    }
}
