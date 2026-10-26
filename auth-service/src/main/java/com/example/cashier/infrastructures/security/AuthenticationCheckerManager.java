package com.example.cashier.infrastructures.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.example.cashier.applications.security.AuthenticationChecker;

@Component
public class AuthenticationCheckerManager implements AuthenticationChecker {

    private final AuthenticationManager authenticationManager;

    public AuthenticationCheckerManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void verify(String username, String password) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
