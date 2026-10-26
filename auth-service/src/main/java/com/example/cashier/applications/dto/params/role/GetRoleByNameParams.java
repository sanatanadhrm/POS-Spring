package com.example.cashier.applications.dto.params.role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetRoleByNameParams {
    public GetRoleByNameParams(String name) {
        this.name = name;
    }

    private String name;
}
