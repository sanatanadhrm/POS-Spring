package com.example.cashier.domains.auth.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "authentications")
public class Authentication {

    public Authentication(String token) {
        this.token = token;
    }

    public Authentication() {
    }

    @Id
    private String token;

}
