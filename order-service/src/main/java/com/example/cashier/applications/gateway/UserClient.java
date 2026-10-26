package com.example.cashier.applications.gateway;

import com.example.cashier.applications.dto.response.user.GetDetailUserResponse;

public interface UserClient {
    GetDetailUserResponse getDetailUserByUsername(String username);
}
