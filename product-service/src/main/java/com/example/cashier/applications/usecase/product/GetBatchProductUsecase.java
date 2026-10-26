package com.example.cashier.applications.usecase.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.request.product.GetBatchProductRequest;
import com.example.cashier.applications.dto.response.product.GetBatchProductResponse;
import com.example.cashier.applications.mapper.ProductMapper;
import com.example.cashier.domains.product.ProductRepository;
import com.example.cashier.domains.product.entity.Product;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GetBatchProductUsecase {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public GetBatchProductUsecase(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<GetBatchProductResponse> execute(GetBatchProductRequest payload) {
        log.info("checkout order batch product: {}", payload);
        List<Product> products = this.productRepository.findByStoreIdAndProductIds(
                payload.getProductIds(),
                payload.getStoreId());
        log.info("success get batch product");
        return this.productMapper.toGetBatchProductResponse(products);
    }
}
