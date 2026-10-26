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
    private String authServiceUrl;
    private String productServiceUrl;

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

    public String getAuthServiceUrl() {
        return this.authServiceUrl;
    }

    public void setAuthServiceUrl(String authServiceUrl) {
        this.authServiceUrl = authServiceUrl;
    }

    public String getProductServiceUrl() {
        return this.productServiceUrl;
    }

    public void setProductServiceUrl(String productServiceUrl) {
        this.productServiceUrl = productServiceUrl;
    }
}
