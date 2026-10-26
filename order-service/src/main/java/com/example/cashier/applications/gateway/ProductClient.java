package com.example.cashier.applications.gateway;

import java.util.List;

import com.example.cashier.applications.dto.request.product.DeductBatchProductRequest;
import com.example.cashier.applications.dto.request.product.GetBatchProductRequest;
import com.example.cashier.applications.dto.response.product.GetBatchProductResponse;

public interface ProductClient {
    List<GetBatchProductResponse> getBatchProduct(GetBatchProductRequest request);

    void deductBatchProduct(DeductBatchProductRequest request);
}
