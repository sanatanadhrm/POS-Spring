package com.example.cashier.applications.dto.request.order;

import java.math.BigDecimal;
import java.util.List;

import com.example.cashier.applications.dto.request.orderitem.CheckoutOrderItemRequest;
import com.example.cashier.domains.orders.entity.OrderPaymentMethod;
import com.example.cashier.domains.orders.entity.OrderType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class CheckoutOrderRequest {

    @NotNull(message = "Payment Method is required")
    private OrderPaymentMethod paymentMethod;

    @NotNull(message = "Order Type is required")
    private OrderType orderType;

    private String tableNumber;

    @NotNull(message = "Cash Received is required")
    @PositiveOrZero(message = "Cash received tidak boleh negatif")
    private BigDecimal cashReceive;

    @NotEmpty(message = "Items pesanan tidak boleh kosong")
    @Valid
    private List<CheckoutOrderItemRequest> items;

    public CheckoutOrderRequest(@NotNull(message = "Payment Method is required") OrderPaymentMethod paymentMethod,
            @NotNull(message = "Order Type is required") OrderType orderType, String tableNumber,
            @NotNull(message = "Cash Received is required") @PositiveOrZero(message = "Cash received tidak boleh negatif") BigDecimal cashReceive,
            @NotEmpty(message = "Items pesanan tidak boleh kosong") @Valid List<CheckoutOrderItemRequest> items) {
        this.paymentMethod = paymentMethod;
        this.orderType = orderType;
        this.tableNumber = tableNumber;
        this.cashReceive = cashReceive;
        this.items = items;
    }

}
