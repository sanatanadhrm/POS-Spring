package com.example.cashier.applications.security;

public interface AuthenticationChecker {
    void verify(String username, String password);
}
