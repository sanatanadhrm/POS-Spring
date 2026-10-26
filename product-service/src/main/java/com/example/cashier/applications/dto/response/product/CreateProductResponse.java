package com.example.cashier.applications.dto.response.product;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class CreateProductResponse {
    private String id;
    private String name;

    public CreateProductResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
