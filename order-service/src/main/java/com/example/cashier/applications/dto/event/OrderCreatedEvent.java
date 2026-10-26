package com.example.cashier.applications.dto.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event DTO yang dikirimkan ke antrean RabbitMQ saat sebuah order berhasil di-checkout.
 * Membawa payload transaksi yang dibutuhkan oleh background worker (notifikasi, cetak struk, audit akuntansi).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable {

    private String orderId;
    private String storeId;
    private String cashierId;
    private String orderType;
    private String paymentMethod;
    private String tableNumber;
    private BigDecimal totalPrice;
    private Integer totalItems;
    private LocalDateTime createdAt;
}
