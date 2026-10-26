package com.example.cashier.applications.dto.request.product;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class DeductBatchItemProductRequest {
    private String productId;
    private int qty;
    private String variantId;

    public DeductBatchItemProductRequest(String productId, int qty, String variantId) {
        this.productId = productId;
        this.qty = qty;
        this.variantId = variantId;
    }

}
