package com.example.cashier.applications.dto.request.product;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class DeductBatchProductRequest {

    private String storeId;

    private List<DeductBatchItemProductRequest> items;

    public DeductBatchProductRequest(String storeId, List<DeductBatchItemProductRequest> items) {
        this.storeId = storeId;
        this.items = items;
    }

}
