package com.example.cashier.interfaces.role;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cashier.applications.dto.params.role.GetRoleByNameParams;
import com.example.cashier.applications.dto.response.role.GetRoleByNameResponse;
import com.example.cashier.applications.usecase.role.GetRoleByNameUseCase;
import com.example.cashier.commons.dto.WebResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleHandler {
    private final GetRoleByNameUseCase getRoleByNameUseCase;

    public RoleHandler(GetRoleByNameUseCase getRoleByNameUseCase) {
        this.getRoleByNameUseCase = getRoleByNameUseCase;
    }

    @GetMapping("/name")
    public WebResponse<GetRoleByNameResponse> getRoleByName(@Valid @ModelAttribute GetRoleByNameParams params) {
        GetRoleByNameResponse response = this.getRoleByNameUseCase.execute(params);
        return WebResponse.<GetRoleByNameResponse>builder()
                .status("Success")
                .message("Role fetched successfully")
                .data(response)
                .build();
    }
}
