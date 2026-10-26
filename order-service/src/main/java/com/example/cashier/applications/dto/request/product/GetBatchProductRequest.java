package com.example.cashier.applications.dto.request.product;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class GetBatchProductRequest {

    private String storeId;

    private List<String> productIds;

    public GetBatchProductRequest(String storeId, List<String> productIds) {
        this.storeId = storeId;
        this.productIds = productIds;
    }

}
