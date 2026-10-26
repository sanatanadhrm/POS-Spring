package com.example.cashier.infrastructures.database.postgresql.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.cashier.domains.product.entity.Product;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

public interface SpringDataProductJpaRepository extends JpaRepository<Product, String> {
    Optional<Product> findById(String id);

    List<Product> findByStoreId(String storeId);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.qty = p.qty - :qty, p.updatedAt = CURRENT_TIMESTAMP WHERE p.id = :productId AND p.qty >= :qty AND p.storeId = :storeId")
    int deductQty(String productId, Integer qty, String storeId);

    Page<Product> findAllByStoreId(String storeId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.variants v " +
            "WHERE p.storeId = :storeId AND p.id IN :productIds")
    List<Product> findByStoreIdAndProductIdsIn(
            @Param("productIds") List<String> productIds,
            @Param("storeId") String storeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds AND p.storeId = :storeId ORDER BY p.id ASC")
    List<Product> findByIdsForUpdate(
            @Param("productIds") List<String> productIds,
            @Param("storeId") String storeId);
}
