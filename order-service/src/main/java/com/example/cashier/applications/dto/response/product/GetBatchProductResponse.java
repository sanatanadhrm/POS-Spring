package com.example.cashier.applications.dto.response.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.cashier.applications.dto.response.productvariant.GetBatchProductVariantResponse;
import com.example.cashier.applications.dto.type.ProductStatusEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetBatchProductResponse {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer qty;
    private String storeId;
    private ProductStatusEnum status;

    @Builder.Default
    private List<GetBatchProductVariantResponse> variants = new ArrayList<>();

    public GetBatchProductResponse(String id, String name, BigDecimal price, Integer qty, String storeId,
            ProductStatusEnum status, List<GetBatchProductVariantResponse> variants) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.storeId = storeId;
        this.status = status;
        this.variants = variants;
    }

}
