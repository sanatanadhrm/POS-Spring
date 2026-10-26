package com.example.cashier.applications.dto.response.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.cashier.applications.dto.response.orderitem.CheckoutOrderItemResponse;
import com.example.cashier.domains.orders.entity.OrderPaymentMethod;
import com.example.cashier.domains.orders.entity.OrderType;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class CheckoutOrderResponse {
    private String id;
    private String storeId;
    private String cashierId;
    private OrderType orderType;
    private String tableNumber;
    private OrderPaymentMethod paymentMethod;
    private BigDecimal cashReceived;
    private BigDecimal changeAmount;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private List<CheckoutOrderItemResponse> items;

    public CheckoutOrderResponse(String id, String storeId, String cashierId, OrderType orderType, String tableNumber,
            OrderPaymentMethod paymentMethod, BigDecimal cashReceived, BigDecimal changeAmount, BigDecimal totalPrice,
            LocalDateTime createdAt, List<CheckoutOrderItemResponse> items) {
        this.id = id;
        this.storeId = storeId;
        this.cashierId = cashierId;
        this.orderType = orderType;
        this.tableNumber = tableNumber;
        this.paymentMethod = paymentMethod;
        this.cashReceived = cashReceived;
        this.changeAmount = changeAmount;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.items = items;
    }

}
