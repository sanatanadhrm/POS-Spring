package com.example.cashier.applications.dto.response.user;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class GetDetailUserResponse {

    public GetDetailUserResponse(String id, String username, String roleId, String roleName, String storeId,
            String storeName) {
        this.id = id;
        this.username = username;
        this.roleId = roleId;
        this.roleName = roleName;
        this.storeId = storeId;
        this.storeName = storeName;
    }

    private String id;
    private String username;
    private String roleId;
    private String roleName;
    private String storeId;
    private String storeName;
}
