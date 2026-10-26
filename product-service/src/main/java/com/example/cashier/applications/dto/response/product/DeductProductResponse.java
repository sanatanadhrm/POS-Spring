package com.example.cashier.applications.dto.response.product;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class DeductProductResponse {
    private String productId;
    private Integer qty;
    private String storeId;

    public DeductProductResponse(String productId, Integer qty, String storeId) {
        this.productId = productId;
        this.qty = qty;
        this.storeId = storeId;
    }

}
