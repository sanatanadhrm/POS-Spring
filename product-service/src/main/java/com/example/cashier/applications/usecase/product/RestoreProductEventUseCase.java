package com.example.cashier.applications.usecase.product;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.event.product.RestoreProductEvent;
import com.example.cashier.applications.dto.event.product.RestoreProductItemEvent;
import com.example.cashier.commons.exception.BadRequestException;
import com.example.cashier.domains.product.ProductRepository;
import com.example.cashier.domains.product.entity.Product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestoreProductEventUseCase {
    private final ProductRepository productRepository;

    @Transactional
    public void execute(RestoreProductEvent payload) {
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

        for (RestoreProductItemEvent item : payload.getItems()) {
            Product product = productMap.get(item.getProductId());
            product.addStock(item.getQty());
        }

        this.productRepository.saveAll(products);

    }
}
