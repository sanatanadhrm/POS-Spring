package com.example.cashier.applications.usecase.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cashier.applications.dto.request.product.DeductProductRequest;
import com.example.cashier.applications.dto.response.product.DeductProductResponse;

import com.example.cashier.domains.product.ProductRepository;
import com.example.cashier.domains.product.entity.Product;

@Service

public class DeductProductUsecase {
    private final ProductRepository productRepository;

    public DeductProductUsecase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public DeductProductResponse execute(DeductProductRequest payload, String productId) {
        Product product = this.productRepository.findById(productId);

        product.verifyStock(payload.getQty());

        this.productRepository.deductQty(productId, payload.getQty(), payload.getStoreId());
        return DeductProductResponse.builder()
                .productId(productId)
                .qty(product.getQty() - payload.getQty())
                .storeId(payload.getStoreId())
                .build();

    }
}
