package com.example.cashier.applications.dto.params.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class GetListProductParams {

    @NotBlank(message = "store id is required")
    private String storeId;

    @NotNull(message = "limit is required")
    @Min(value = 1, message = "limit must be at least 1")
    private int limit;

    @NotNull(message = "page is required")
    @Min(value = 1, message = "page must be at least 1")
    private int page;

    public GetListProductParams(@NotBlank(message = "store id is required") String storeId,
            @NotNull(message = "limit is required") @Min(value = 1, message = "limit must be at least 1") int limit,
            @NotNull(message = "page is required") @Min(value = 1, message = "page must be at least 1") int page) {
        this.storeId = storeId;
        this.limit = limit;
        this.page = page;
    }

}
