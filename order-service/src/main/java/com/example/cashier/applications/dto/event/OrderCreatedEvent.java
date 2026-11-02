package com.example.cashier.applications.dto.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event DTO yang dikirimkan ke antrean RabbitMQ saat sebuah order berhasil
 * di-checkout.
 * Membawa payload transaksi yang dibutuhkan oleh background worker (notifikasi,
 * cetak struk, audit akuntansi).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable {

    private String orderId;
    private String storeName;
    private String cashierName;
    private String orderType;
    private String paymentMethod;
    private String tableNumber;
    private BigDecimal totalPrice;
    private BigDecimal cashReceive;
    private BigDecimal change;
    private Integer totalItems;
    private String createdAt;
    private List<OrderCreatedEventItem> items;

}
