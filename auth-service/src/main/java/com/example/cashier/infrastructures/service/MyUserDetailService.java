package com.example.cashier.infrastructures.service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.cashier.domains.user.entity.User;
import com.example.cashier.infrastructures.repository.SpringDataUserJpaRepository;

@Primary
@Service
public class MyUserDetailService implements UserDetailsService {

    private final SpringDataUserJpaRepository userRepository;

    public MyUserDetailService(SpringDataUserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }

}
