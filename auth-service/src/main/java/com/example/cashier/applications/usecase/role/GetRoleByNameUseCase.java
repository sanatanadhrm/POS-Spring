package com.example.cashier.applications.usecase.role;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.params.role.GetRoleByNameParams;
import com.example.cashier.applications.dto.response.role.GetRoleByNameResponse;
import com.example.cashier.domains.role.RoleRepository;
import com.example.cashier.domains.role.entity.Role;

@Service
public class GetRoleByNameUseCase {
    private final RoleRepository roleRepository;

    public GetRoleByNameUseCase(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public GetRoleByNameResponse execute(GetRoleByNameParams params) {
        Role role = this.roleRepository.getByRoleName(params.getName());
        return GetRoleByNameResponse.builder()
                .name(role.getName())
                .id(role.getId())
                .build();
    }
}
