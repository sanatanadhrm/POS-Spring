package com.example.cashier.applications.dto.response.role;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@Builder
public class GetRoleByNameResponse {

    public GetRoleByNameResponse(String name, String id) {
        this.name = name;
        this.id = id;
    }

    private String name;
    private String id;
}
