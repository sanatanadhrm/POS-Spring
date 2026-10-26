package com.example.cashier.applications.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetDetailUserByUsernameRequest {

    public GetDetailUserByUsernameRequest(String username) {
        this.username = username;
    }

    @NotBlank(message = "username is required")
    private String username;
}
