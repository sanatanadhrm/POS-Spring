package com.example.cashier.applications.dto.notification.receipt;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptData {
    private String orderId;
    private String storeName;
    private String storeAddress;
    private String cashierName;
    private String orderType;
    private String tableNumber;
    private String transactionDate;
    private BigDecimal totalPrice;
    private BigDecimal cashReceived;
    private BigDecimal changeAmount;
    private String paymentMethod;
    private List<ReceiptItemData> items;
}
