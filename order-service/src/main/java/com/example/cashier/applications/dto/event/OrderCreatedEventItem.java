package com.example.cashier.applications.dto.event;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEventItem {
    private String productName;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
