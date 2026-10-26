package com.example.cashier.applications.dto.response.productvariant;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class GetBatchProductVariantResponse {
    private String id;
    private String name;
    private BigDecimal additionalPrice;

    public GetBatchProductVariantResponse(String id, String name, BigDecimal additionalPrice) {
        this.id = id;
        this.name = name;
        this.additionalPrice = additionalPrice;
    }

}
