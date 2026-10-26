package com.example.cashier.applications.dto.response.product;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class GetListProductResponse {
    private String id;
    private String name;
    private Integer qty;
    private String storeId;
    private String status;
    private BigDecimal price;

    public GetListProductResponse(String id, String name, Integer qty, String storeId, String status,
            BigDecimal price) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.storeId = storeId;
        this.status = status;
        this.price = price;
    }

}
