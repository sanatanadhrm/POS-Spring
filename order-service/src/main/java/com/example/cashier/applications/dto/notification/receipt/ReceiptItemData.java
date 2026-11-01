package com.example.cashier.applications.dto.notification.receipt;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemData {
    private String productName;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
