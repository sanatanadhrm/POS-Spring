package com.example.cashier.applications.dto.request.product;

import java.math.BigDecimal;

import com.example.cashier.domains.product.entity.ProductStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must be at least 3 characters")
    private String name;

    @NotNull(message = "Qty is required")
    @Min(value = 0, message = "Qty cannot be zero or negative")
    private Integer qty;

    @NotBlank(message = "StoreId is required")
    private String storeId;
    @NotNull(message = "Status is required")
    private ProductStatus status;

    @NotNull(message = "Price is required")
    @Positive(message = "Price cannot be zero or negative")
    private BigDecimal price;

    public CreateProductRequest(
            @NotBlank(message = "Name is required") @Size(min = 3, message = "Name must be at least 3 characters") String name,
            @NotNull(message = "Qty is required") @Min(value = 0, message = "Qty cannot be zero or negative") Integer qty,
            @NotBlank(message = "StoreId is required") String storeId,
            @NotNull(message = "Status is required") ProductStatus status,
            @NotNull(message = "Price is required") @Positive(message = "Price cannot be zero or negative") BigDecimal price) {
        this.name = name;
        this.qty = qty;
        this.storeId = storeId;
        this.status = status;
        this.price = price;
    }

}
