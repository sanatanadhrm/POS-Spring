package com.example.cashier.applications.usecase.user;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.response.user.GetDetailUserResponse;
import com.example.cashier.domains.user.UserRepository;
import com.example.cashier.domains.user.entity.User;

@Service
public class GetDetailUserUseCase {
    private final UserRepository userRepository;

    public GetDetailUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public GetDetailUserResponse execute(String userId) {
        User user = this.userRepository.getByUserId(userId);
        return GetDetailUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getName())
                .storeId(user.getStore().getId())
                .storeName(user.getStore().getName())
                .build();
    }
}
