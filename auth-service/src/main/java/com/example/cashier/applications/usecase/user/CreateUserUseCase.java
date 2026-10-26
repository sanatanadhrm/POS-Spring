package com.example.cashier.applications.usecase.user;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.request.user.CreateUserRequest;
import com.example.cashier.applications.dto.response.user.CreateUserResponse;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.domains.role.RoleRepository;
import com.example.cashier.domains.role.entity.Role;
import com.example.cashier.domains.store.StoreRepository;
import com.example.cashier.domains.store.entity.Store;
import com.example.cashier.domains.user.UserRepository;
import com.example.cashier.domains.user.entity.User;

@Service
public class CreateUserUseCase {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public CreateUserUseCase(RoleRepository roleRepository, UserRepository userRepository,
            StoreRepository storeRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    public CreateUserResponse execute(CreateUserRequest request, String targetRole) {

        User user = userRepository.findByUsername(request.getUsername());

        if (user != null) {
            throw new NotFoundException("User already exists");
        }

        Role role = roleRepository.getByRoleId(request.getRoleId());

        if (role == null) {
            throw new NotFoundException("Role not found");
        }

        Store store = storeRepository.getByStoreId(request.getStoreId());

        if (store == null) {
            throw new NotFoundException("Store not found");
        }

        if (role.getName().equals("SUPER ADMIN")) {
            throw new UnauthorizedException("You don't have permission to create SUPER ADMIN");
        }

        if (!role.getName().equals(targetRole.toUpperCase())) {
            throw new UnauthorizedException("You don't have permission to create this role");
        }

        User userBuild = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(role)
                .store(store)
                .build();

        this.userRepository.save(userBuild);

        return CreateUserResponse.builder()
                .userId(userBuild.getId())
                .userName(userBuild.getUsername())
                .roleName(role.getName())
                .storeName(store.getName())
                .build();

    }
}
