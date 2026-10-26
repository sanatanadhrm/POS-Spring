package com.example.cashier.interfaces.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cashier.applications.dto.request.user.CreateCustomerRequest;
import com.example.cashier.applications.dto.request.user.CreateUserRequest;
import com.example.cashier.applications.dto.request.user.GetDetailUserByUsernameRequest;
import com.example.cashier.applications.dto.response.user.CreateUserResponse;
import com.example.cashier.applications.dto.response.user.GetDetailUserResponse;
import com.example.cashier.applications.usecase.user.CreateCustomerUseCase;
import com.example.cashier.applications.usecase.user.CreateUserUseCase;
import com.example.cashier.applications.usecase.user.GetDetailUserByUsernameUseCase;
import com.example.cashier.applications.usecase.user.GetDetailUserUseCase;
import com.example.cashier.commons.dto.WebResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserHandler {

    private final CreateUserUseCase createUserUseCase;
    private final CreateCustomerUseCase createCustomerUsecase;
    private final GetDetailUserUseCase getDetailUserUseCase;
    private final GetDetailUserByUsernameUseCase getDetailUserByUsernameUseCase;

    public UserHandler(CreateUserUseCase createUserUseCase, CreateCustomerUseCase createCustomerUsecase,
            GetDetailUserUseCase getDetailUserUseCase, GetDetailUserByUsernameUseCase getDetailUserByUsernameUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.createCustomerUsecase = createCustomerUsecase;
        this.getDetailUserUseCase = getDetailUserUseCase;
        this.getDetailUserByUsernameUseCase = getDetailUserByUsernameUseCase;
    }

    @PreAuthorize("hasRole('SUPER ADMIN')")
    @PostMapping("/owner")
    public WebResponse<CreateUserResponse> createOwner(@RequestBody CreateUserRequest request) {

        CreateUserResponse response = this.createUserUseCase.execute(request, "OWNER");

        return WebResponse.<CreateUserResponse>builder()
                .status("Success")
                .message("User owner created successfully")
                .data(response)
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/cashier")
    public WebResponse<CreateUserResponse> createCashier(@RequestBody CreateUserRequest request) {

        CreateUserResponse response = this.createUserUseCase.execute(request, "CASHIER");

        return WebResponse.<CreateUserResponse>builder()
                .status("Success")
                .message("User cashier created successfully")
                .data(response)
                .build();
    }

    @PostMapping("/customer")
    public WebResponse<CreateUserResponse> createCustomer(@RequestBody CreateCustomerRequest request) {

        CreateUserResponse response = this.createCustomerUsecase.execute(request);

        return WebResponse.<CreateUserResponse>builder()
                .status("Success")
                .message("User customer created successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public WebResponse<GetDetailUserResponse> getDetailUser(@PathVariable String id) {
        GetDetailUserResponse response = this.getDetailUserUseCase.execute(id);
        return WebResponse.<GetDetailUserResponse>builder()
                .status("Success")
                .message("User detail fetched successfully")
                .data(response)
                .build();
    }

    @GetMapping("/username")
    public WebResponse<GetDetailUserResponse> getDetailUserbyUsername(
            @Valid @ModelAttribute GetDetailUserByUsernameRequest params) {
        GetDetailUserResponse response = this.getDetailUserByUsernameUseCase.execute(params);
        return WebResponse.<GetDetailUserResponse>builder()
                .status("Success")
                .message("User detail fetched successfully")
                .data(response)
                .build();
    }
}
