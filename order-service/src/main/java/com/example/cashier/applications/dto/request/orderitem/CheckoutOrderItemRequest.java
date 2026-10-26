package com.example.cashier.applications.dto.request.orderitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class CheckoutOrderItemRequest {
    @NotBlank(message = "Product ID is required")
    private String productId;

    private String productVariantId;

    @NotNull(message = "Qty wajib diisi")
    @Min(value = 1, message = "Qty minimal 1")
    private Integer qty;

    public CheckoutOrderItemRequest(@NotBlank(message = "Product ID is required") String productId,
            String productVariantId,
            @NotNull(message = "Qty wajib diisi") @Min(value = 1, message = "Qty minimal 1") Integer qty) {
        this.productId = productId;
        this.productVariantId = productVariantId;
        this.qty = qty;
    }

}
