package com.example.cashier.infrastructures.gateway.auth;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.cashier.applications.dto.response.user.GetDetailUserResponse;
import com.example.cashier.applications.gateway.UserClient;
import com.example.cashier.commons.dto.WebResponse;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.commons.exception.UnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserHttpGateway implements UserClient {

    private final RestClient authRestClient;

    public UserHttpGateway(RestClient authRestClient) {
        this.authRestClient = authRestClient;
    }

    @Override
    @CircuitBreaker(name = "authService", fallbackMethod = "getDetailUserFallback")
    public GetDetailUserResponse getDetailUserByUsername(String username) {
        log.info("Username to find: {}", username);
        WebResponse<GetDetailUserResponse> response = this.authRestClient.get()
                .uri(builder -> builder.path("/users/username")
                        .queryParam("username", username)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new NotFoundException("User with username " + username + " is not found!");
                })
                .onStatus(status -> status.value() == 401 || status.value() == 403, (req, res) -> {
                    throw new UnauthorizedException(
                            "Akses ditolak: Token autentikasi tidak valid atau sudah kadaluarsa!");
                })
                .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                    throw new RuntimeException("Failed to contact Auth Service. Please try again.");
                })
                .body(new ParameterizedTypeReference<WebResponse<GetDetailUserResponse>>() {
                });
        log.info("Data response UserHttpGateway: {}", response.getData());
        return response.getData();
    }

    public GetDetailUserResponse getDetailUserFallback(String username, Throwable ex) {
        log.error("User Service is DOWN/BUSY! Triggering fallback. Reason: {}", ex.getMessage());
        throw new UnavailableException("Layanan User sedang tidak dapat diakses, silakan coba sesaat lagi.");
    }

}
