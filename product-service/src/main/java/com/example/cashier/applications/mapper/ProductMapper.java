package com.example.cashier.applications.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.response.product.GetBatchProductResponse;
import com.example.cashier.applications.dto.response.product.GetListProductResponse;
import com.example.cashier.applications.dto.response.productvariant.GetBatchProductVariantResponse;
import com.example.cashier.domains.product.entity.Product;
import com.example.cashier.domains.product.entity.ProductVariant;

@Component
public class ProductMapper {

    public List<GetListProductResponse> toGetListProductResponse(List<Product> products) {
        return products.stream().map(this::toGetListProductResponse)
                .collect(Collectors.toList());
    }

    private GetListProductResponse toGetListProductResponse(Product product) {
        return GetListProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .qty(product.getQty())
                .storeId(product.getStoreId())
                .status(product.getStatus().name())
                .price(product.getPrice())
                .build();
    }

    public List<GetBatchProductResponse> toGetBatchProductResponse(List<Product> products) {
        return products.stream().map(this::toGetBatchProductResponse)
                .collect(Collectors.toList());
    }

    private GetBatchProductResponse toGetBatchProductResponse(Product product) {
        return GetBatchProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .qty(product.getQty())
                .storeId(product.getStoreId())
                .status(product.getStatus())
                .price(product.getPrice())
                .variants(product.getVariants().stream().map(this::toGetBatchProductVariantResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private GetBatchProductVariantResponse toGetBatchProductVariantResponse(ProductVariant variant) {
        return GetBatchProductVariantResponse.builder()
                .id(variant.getId())
                .name(variant.getName())
                .additionalPrice(variant.getAdditionalPrice())
                .build();
    }
}
