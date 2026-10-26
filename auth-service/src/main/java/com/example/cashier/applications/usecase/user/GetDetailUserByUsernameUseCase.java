package com.example.cashier.applications.usecase.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.request.user.GetDetailUserByUsernameRequest;
import com.example.cashier.applications.dto.response.user.GetDetailUserResponse;
import com.example.cashier.commons.exception.NotFoundException;

import com.example.cashier.domains.user.UserRepository;
import com.example.cashier.domains.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GetDetailUserByUsernameUseCase {

    private final UserRepository userRepository;

    public GetDetailUserByUsernameUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public GetDetailUserResponse execute(GetDetailUserByUsernameRequest params) {
        User user = userRepository.findByUsername(params.getUsername());

        if (user == null) {
            log.info("username not found");
            throw new NotFoundException("Username not found");
        }
        log.info("Checkout order request User: {}", user.getUsername());
        log.info("Checkout order request User: {}", user.getRole().getName());
        GetDetailUserResponse response = GetDetailUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roleId(Optional.ofNullable(user.getRole()).map(role -> role.getId()).orElse(null))
                .roleName(Optional.ofNullable(user.getRole()).map(role -> role.getName()).orElse(null))
                .storeId(Optional.ofNullable(user.getStore()).map(store -> store.getId()).orElse(null))
                .storeName(Optional.ofNullable(user.getStore()).map(store -> store.getName()).orElse(null))
                .build();
        log.info("Checkout order request User: {}", response);
        return response;
    }
}
