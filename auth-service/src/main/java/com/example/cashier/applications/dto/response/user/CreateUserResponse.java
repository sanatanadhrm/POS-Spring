package com.example.cashier.applications.dto.response.user;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class CreateUserResponse {

    public CreateUserResponse(String userId, String userName, String roleName, String storeName) {
        this.userId = userId;
        this.userName = userName;
        this.roleName = roleName;
        this.storeName = storeName;
    }

    private String userId;
    private String userName;
    private String roleName;
    private String storeName;
}
