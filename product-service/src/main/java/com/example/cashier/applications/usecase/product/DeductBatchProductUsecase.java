package com.example.cashier.applications.usecase.product;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cashier.applications.dto.request.product.DeductBatchItemProductRequest;
import com.example.cashier.applications.dto.request.product.DeductBatchProductRequest;
import com.example.cashier.applications.dto.response.product.DeductProductResponse;
import com.example.cashier.commons.exception.BadRequestException;
import com.example.cashier.domains.product.ProductRepository;
import com.example.cashier.domains.product.entity.Product;

@Service
public class DeductBatchProductUsecase {

    private final ProductRepository productRepository;

    public DeductBatchProductUsecase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public List<DeductProductResponse> execute(DeductBatchProductRequest payload) {
        List<String> productIds = payload.getItems().stream()
                .map(item -> item.getProductId())
                .sorted()
                .toList();
        List<Product> products = this.productRepository.findByIdsForUpdate(productIds, payload.getStoreId());

        if (products.size() != payload.getItems().size()) {
            throw new BadRequestException("Some product not found");
        }

        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(product -> product.getId(), Function.identity()));

        for (DeductBatchItemProductRequest item : payload.getItems()) {
            Product product = productMap.get(item.getProductId());
            product.deductStock(item.getQty());
        }

        this.productRepository.saveAll(products);

        List<DeductProductResponse> responses = products.stream()
                .map(prod -> DeductProductResponse.builder()
                        .productId(prod.getId())
                        .qty(prod.getQty())
                        .storeId(payload.getStoreId())
                        .build())
                .toList();

        return responses;
    }

}
