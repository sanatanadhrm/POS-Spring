package com.example.cashier.applications.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCustomerRequest {

    public CreateCustomerRequest(
            @NotBlank(message = "Username is required") @Size(min = 5, max = 50, message = "Username must be at least 5 and at most 50 characters") String username,
            @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one number") String password,
            @NotBlank(message = "Role is required") String roleId) {
        this.username = username;
        this.password = password;
        this.roleId = roleId;
    }

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 50, message = "Username must be at least 5 and at most 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one number")
    private String password;

    @NotBlank(message = "Role is required")
    private String roleId;

}
