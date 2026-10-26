package com.example.cashier.infrastructures.database.postgresql.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.example.cashier.commons.exception.ConflictException;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.domains.commons.PagedResult;
import com.example.cashier.domains.product.ProductRepository;
import com.example.cashier.domains.product.entity.Product;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProductRepositoryPostgreImpl implements ProductRepository {
    private final SpringDataProductJpaRepository springDataProductJpaRepository;

    public ProductRepositoryPostgreImpl(SpringDataProductJpaRepository springDataProductJpaRepository) {
        this.springDataProductJpaRepository = springDataProductJpaRepository;
    }

    @Override
    public void save(Product product) {
        this.springDataProductJpaRepository.save(product);
    }

    @Override
    public Product findById(String id) {
        return this.springDataProductJpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with ID: " + id + " not found"));
    }

    @Override
    public void delete(Product product) {
        this.springDataProductJpaRepository.delete(product);
    }

    @Override
    public List<Product> findByStoreId(String storeId) {
        return this.springDataProductJpaRepository.findByStoreId(storeId);
    }

    @Override
    public void deductQty(String productId, Integer qty, String storeId) {
        int result = this.springDataProductJpaRepository.deductQty(productId, qty, storeId);

        if (result == 0) {
            throw new ConflictException("Produk " + productId + " tidak tersedia");
        }
    }

    @Override
    public PagedResult<Product> findByStoreIdWithPagination(String storeId, int limit, int page) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Product> pageResult = this.springDataProductJpaRepository.findAllByStoreId(storeId, pageable);

        return PagedResult.<Product>builder()
                .data(pageResult.getContent())
                .page(pageResult.getNumber() + 1)
                .limit(pageResult.getSize())
                .totalRows(pageResult.getTotalElements())
                .build();

    }

    @Override
    public List<Product> findByStoreIdAndProductIds(List<String> productIds, String storeId) {
        return this.springDataProductJpaRepository.findByStoreIdAndProductIdsIn(productIds, storeId);
    }

    @Override
    public List<Product> findByIdsForUpdate(List<String> productIds, String storeId) {
        return this.springDataProductJpaRepository.findByIdsForUpdate(productIds, storeId);
    }

    @Override
    public void saveAll(List<Product> products) {
        this.springDataProductJpaRepository.saveAll(products);
    }
}
