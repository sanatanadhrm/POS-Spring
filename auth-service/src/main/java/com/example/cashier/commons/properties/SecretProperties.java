package com.example.cashier.commons.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:secret.properties")
@ConfigurationProperties(prefix = "app.rahasia")
public class SecretProperties {
    private String jwtSecret;
    private int accessTokenExpiredDuration;
    private int refreshTokenExpiredDuration;

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public void setAccessTokenExpiredDuration(int accessTokenExpiredDuration) {
        this.accessTokenExpiredDuration = accessTokenExpiredDuration;
    }

    public void setRefreshTokenExpiredDuration(int refreshTokenExpiredDuration) {
        this.refreshTokenExpiredDuration = refreshTokenExpiredDuration;
    }

    public String getJwtSecret() {
        return this.jwtSecret;
    }

    public int getAccessTokenExpiredDuration() {
        return this.accessTokenExpiredDuration;
    }

    public int getRefreshTokenExpiredDuration() {
        return this.refreshTokenExpiredDuration;
    }
}
