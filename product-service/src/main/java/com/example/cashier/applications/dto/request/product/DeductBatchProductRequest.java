package com.example.cashier.applications.dto.request.product;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeductBatchProductRequest {
    @NotBlank(message = "storeId is required")
    private String storeId;

    @NotNull(message = "items is required")
    @NotEmpty(message = "items is required")
    private List<DeductBatchItemProductRequest> items;

    public DeductBatchProductRequest(@NotBlank(message = "storeId is required") String storeId,
            @NotNull(message = "items is required") @NotEmpty(message = "items is required") List<DeductBatchItemProductRequest> items) {
        this.storeId = storeId;
        this.items = items;
    }

}
