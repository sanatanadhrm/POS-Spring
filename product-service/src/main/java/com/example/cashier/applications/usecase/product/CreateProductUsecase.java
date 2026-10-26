package com.example.cashier.applications.usecase.product;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.request.product.CreateProductRequest;
import com.example.cashier.applications.dto.response.product.CreateProductResponse;
import com.example.cashier.applications.gateway.StoreClient;
import com.example.cashier.domains.product.ProductRepository;
import com.example.cashier.domains.product.entity.Product;

@Service
public class CreateProductUsecase {
        private final ProductRepository productRepository;
        private final StoreClient storeClient;

        public CreateProductUsecase(ProductRepository productRepository, StoreClient storeClient) {
                this.productRepository = productRepository;
                this.storeClient = storeClient;
        }

        public CreateProductResponse execute(CreateProductRequest payload) {
                storeClient.verifyStoreId(payload.getStoreId());
                Product product = Product.builder()
                                .id(UUID.randomUUID().toString())
                                .name(payload.getName())
                                .qty(payload.getQty())
                                .storeId(payload.getStoreId())
                                .status(payload.getStatus())
                                .price(payload.getPrice())

                                .build();
                productRepository.save(product);
                return CreateProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .build();
        }

}
