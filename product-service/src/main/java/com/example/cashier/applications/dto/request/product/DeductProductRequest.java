package com.example.cashier.applications.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeductProductRequest {

    @NotNull(message = "qty is required")
    @Min(value = 1, message = "qty must be greater than 0")
    private Integer qty;

    @NotBlank(message = "store_id is required")
    private String storeId;

    public DeductProductRequest(
            @NotNull(message = "qty is required") @Min(value = 1, message = "qty must be greater than 0") Integer qty,
            @NotBlank(message = "store_id is required") String storeId) {
        this.qty = qty;
        this.storeId = storeId;
    }

}
