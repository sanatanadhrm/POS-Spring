package com.example.cashier.infrastructures.gateway.auth;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.cashier.applications.dto.response.role.GetRoleByNameResponse;
import com.example.cashier.applications.gateway.RoleClient;
import com.example.cashier.commons.dto.WebResponse;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.commons.exception.UnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RoleHttpGateway implements RoleClient {

    private final RestClient authRestClient;

    public RoleHttpGateway(RestClient authRestClient) {
        this.authRestClient = authRestClient;
    }

    @Override
    @CircuitBreaker(name = "authService", fallbackMethod = "getRoleByNameFallback")
    public GetRoleByNameResponse getRoleByName(String name) {
        WebResponse<GetRoleByNameResponse> response = this.authRestClient.get()
                .uri(builder -> builder
                        .path("/roles/name")
                        .queryParam("name", name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new NotFoundException("Role with name " + name + " is not found!");
                })
                .onStatus(status -> status.value() == 401 || status.value() == 403, (req, res) -> {
                    throw new UnauthorizedException(
                            "Akses ditolak: Token autentikasi tidak valid atau sudah kadaluarsa!");
                })
                .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                    throw new RuntimeException("Failed to contact Auth Service. Please try again.");
                })
                .body(new ParameterizedTypeReference<WebResponse<GetRoleByNameResponse>>() {

                });

        GetRoleByNameResponse result = response.getData();

        return result;
    }

    public GetRoleByNameResponse getRoleByNameFallback(String name, Throwable ex) {
        log.error("Role Service is DOWN/BUSY! Triggering fallback. Reason: {}", ex.getMessage());
        throw new UnavailableException("Layanan Role sedang tidak dapat diakses, silakan coba sesaat lagi.");
    }

}
