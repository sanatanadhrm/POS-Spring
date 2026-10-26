package com.example.cashier.domains.user;

import com.example.cashier.domains.user.entity.User;

public interface UserRepository {
    void save(User user);

    User findByUsername(String username);

    User getByUserId(String userId);

}
