package com.example.cashier.infrastructures.database.postgresql;

import org.springframework.stereotype.Component;

import com.example.cashier.domains.user.UserRepository;
import com.example.cashier.domains.user.entity.User;
import com.example.cashier.infrastructures.repository.SpringDataUserJpaRepository;

@Component
public class UserRepositoryPostgreImpl implements UserRepository {

    private final SpringDataUserJpaRepository jpaRepository;

    public UserRepositoryPostgreImpl(SpringDataUserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(User user) {
        this.jpaRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return this.jpaRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User getByUserId(String userId) {
        return this.jpaRepository.findById(userId).orElse(null);
    }
}
