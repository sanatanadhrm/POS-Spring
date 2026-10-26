package com.example.cashier.applications.gateway;

import com.example.cashier.applications.dto.response.role.GetRoleByNameResponse;

public interface RoleClient {
    GetRoleByNameResponse getRoleByName(String name);
}
