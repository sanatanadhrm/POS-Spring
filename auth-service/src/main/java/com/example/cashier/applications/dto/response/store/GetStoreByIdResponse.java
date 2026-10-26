package com.example.cashier.applications.dto.response.store;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class GetStoreByIdResponse {

    public GetStoreByIdResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private String id;
    private String name;
}
