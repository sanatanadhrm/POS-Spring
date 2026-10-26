package com.example.cashier.applications.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogOutRequest {
    @NotBlank(message = "Refresh Token is required")
    private String refreshToken;
}
