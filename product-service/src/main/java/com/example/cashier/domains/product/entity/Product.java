package com.example.cashier.domains.product.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.cashier.commons.exception.BadRequestException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "store_id", nullable = false)
    private String storeId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    public Product() {
    }

    public Product(String id, String name, Integer qty, String storeId, ProductStatus status, BigDecimal price,
            LocalDateTime createdAt, LocalDateTime updatedAt, List<ProductVariant> variants) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.storeId = storeId;
        this.status = status;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.variants = variants;
    }

    public void verifyStock(int amount) {
        if (this.qty < amount) {
            throw new BadRequestException("Stok produk tidak mencukupi!");
        }
    }

    public void deductStock(int amount) {
        if (this.qty < amount) {
            throw new BadRequestException("Stok produk tidak mencukupi!");
        }
        this.qty -= amount; // <-- Pengurangan stok
    }

}
