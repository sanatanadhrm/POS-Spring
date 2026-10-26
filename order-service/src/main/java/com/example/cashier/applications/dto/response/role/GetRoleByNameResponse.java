package com.example.cashier.applications.dto.response.role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetRoleByNameResponse {
    private String name;
    private String id;

    public GetRoleByNameResponse(String name, String id) {
        this.name = name;
        this.id = id;
    }

}
