package com.example.cashier.domains.product;

import java.util.List;

import com.example.cashier.domains.commons.PagedResult;
import com.example.cashier.domains.product.entity.Product;

public interface ProductRepository {
    void save(Product product);

    void saveAll(List<Product> products);

    Product findById(String id);

    void delete(Product product);

    List<Product> findByStoreId(String storeId);

    void deductQty(String productId, Integer qty, String storeId);

    PagedResult<Product> findByStoreIdWithPagination(String storeId, int limit, int page);

    List<Product> findByStoreIdAndProductIds(List<String> productIds, String storeId);

    List<Product> findByIdsForUpdate(List<String> productIds, String storeId);
}
