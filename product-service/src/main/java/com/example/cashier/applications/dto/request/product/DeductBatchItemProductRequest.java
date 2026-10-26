package com.example.cashier.applications.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeductBatchItemProductRequest {
    @NotBlank(message = "productId is required")
    private String productId;
    @NotNull(message = "qty is required")
    @Min(value = 1, message = "qty must be greater than 0")
    private int qty;

    public DeductBatchItemProductRequest(@NotBlank(message = "productId is required") String productId,
            @NotNull(message = "qty is required") @Min(value = 1, message = "qty must be greater than 0") int qty) {
        this.productId = productId;
        this.qty = qty;
    }

}
