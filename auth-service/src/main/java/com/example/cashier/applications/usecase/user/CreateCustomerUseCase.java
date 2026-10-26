package com.example.cashier.applications.usecase.user;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.request.user.CreateCustomerRequest;
import com.example.cashier.applications.dto.response.user.CreateUserResponse;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.domains.role.RoleRepository;
import com.example.cashier.domains.role.entity.Role;
import com.example.cashier.domains.user.UserRepository;
import com.example.cashier.domains.user.entity.User;

@Service
public class CreateCustomerUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CreateCustomerUseCase(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public CreateUserResponse execute(CreateCustomerRequest request) {
        User user = userRepository.findByUsername(request.getUsername());

        if (user != null) {
            throw new NotFoundException("User already exists");
        }

        Role role = roleRepository.getByRoleId(request.getRoleId());

        if (role == null) {
            throw new NotFoundException("Role not found");
        }

        if (!role.getName().equals("CUSTOMER")) {
            throw new UnauthorizedException("You don't have permission to create this role");
        }

        User userBuild = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(role)
                .build();

        this.userRepository.save(userBuild);

        return CreateUserResponse.builder()
                .userId(userBuild.getId())
                .userName(userBuild.getUsername())
                .roleName(role.getName())
                .build();
    }
}
