package com.example.cashier.applications.dto.params.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetDetailUserParams {

    public GetDetailUserParams(String roleId) {
        this.roleId = roleId;
    }

    private String roleId;
}
