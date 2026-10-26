package com.example.cashier.applications.dto.request.product;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetBatchProductRequest {

    @NotBlank(message = "storeId is required")
    private String storeId;

    @NotNull(message = "productIds is required")
    @NotEmpty(message = "productIds is required")
    private List<String> productIds;

    public GetBatchProductRequest(@NotBlank(message = "storeId is required") String storeId,
            @NotNull(message = "productIds is required") @NotEmpty(message = "productIds is required") List<String> productIds) {
        this.storeId = storeId;
        this.productIds = productIds;
    }

}
