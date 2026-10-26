package com.example.cashier.applications.dto.response.orderitem;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class CheckoutOrderItemResponse {
    private String productId;
    private String productVariantId;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public CheckoutOrderItemResponse(String productId, String productVariantId, Integer qty, BigDecimal unitPrice,
            BigDecimal subtotal) {
        this.productId = productId;
        this.productVariantId = productVariantId;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

}
