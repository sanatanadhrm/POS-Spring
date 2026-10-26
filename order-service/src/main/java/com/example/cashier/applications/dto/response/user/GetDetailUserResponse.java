package com.example.cashier.applications.dto.response.user;

import lombok.Data;

@Data
public class GetDetailUserResponse {
    private String id;
    private String username;
    private String roleId;
    private String roleName;
    private String storeId;
    private String storeName;
}
