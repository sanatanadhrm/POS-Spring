package com.example.cashier.domains.orders.entity;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.cashier.commons.exception.BadRequestException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "store_id", nullable = false)
    private String storeId;

    @Column(name = "cashier_id", nullable = false)
    private String cashierId;

    @Column(name = "order_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(name = "table_number", nullable = true)
    private String tableNumber;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderPaymentMethod paymentMethod;

    @Column(name = "cash_received", nullable = false)
    private BigDecimal cashReceived;

    @Column(name = "change_amount", nullable = false)
    private BigDecimal changeAmount;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItems> items = new ArrayList<>();

    public Orders(String id, String storeId, String cashierId, OrderType orderType, String tableNumber,
            OrderPaymentMethod paymentMethod, BigDecimal cashReceived, BigDecimal changeAmount, BigDecimal totalPrice,
            String idempotencyKey, LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderItems> items) {
        this.id = id;
        this.storeId = storeId;
        this.cashierId = cashierId;
        this.orderType = orderType;
        this.tableNumber = tableNumber;
        this.paymentMethod = paymentMethod;
        this.cashReceived = cashReceived;
        this.changeAmount = changeAmount;
        this.totalPrice = totalPrice;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items;
    }

    public void addItem(String productId, String productVariant, int qty, BigDecimal unitPrice) {
        OrderItems item = OrderItems.builder()
                .id(UUID.randomUUID().toString())
                .order(this)
                .productId(productId)
                .productVariant(productVariant)
                .qty(qty)
                .unitPrice(unitPrice)
                .build();
        item.calcSubTotal();
        this.items.add(item);
    }

    public void completeCheckout() {
        this.calcTotalAmount();
        this.calcChangeAmount();
    }

    private void calcTotalAmount() {
        if (this.items == null) {
            this.totalPrice = BigDecimal.ZERO;
            return;
        }
        this.totalPrice = this.items.stream()
                .map(item -> item != null && item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, (total, subtotal) -> total.add(subtotal));
    }

    private void calcChangeAmount() {
        if (this.paymentMethod == OrderPaymentMethod.CASH) {
            if (this.cashReceived == null || this.cashReceived.compareTo(this.totalPrice) < 0) {
                throw new BadRequestException("Uang pembayaran kurang!");
            }
            this.changeAmount = this.cashReceived.subtract(this.totalPrice);
        } else {
            this.changeAmount = BigDecimal.ZERO;
        }
    }
}
