package com.example.cashier.infrastructures.gateway;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.cashier.applications.gateway.StoreClient;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.commons.exception.UnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class StoreHttpGateway implements StoreClient {

    private final RestClient authRestClient;

    public StoreHttpGateway(RestClient authRestClient) {
        this.authRestClient = authRestClient;
    }

    @Override
    @CircuitBreaker(name = "authService", fallbackMethod = "verifyStoreIdFallback")
    public void verifyStoreId(String storeId) {
        authRestClient.get()
                .uri("/stores/{storeId}", storeId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new NotFoundException("Store with id " + storeId + " is not found!");
                })
                .onStatus(status -> status.value() == 401 || status.value() == 403, (req, res) -> {
                    throw new UnauthorizedException(
                            "Akses ditolak: Token autentikasi tidak valid atau sudah kadaluarsa!");
                })
                .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                    throw new RuntimeException("Failed to contact Auth Service. Please try again.");
                })
                .toBodilessEntity();
    }

    public void verifyStoreIdFallback(String storeId, Throwable t) {
        throw new UnavailableException("Store Service is currently unavailable. Please try again later.");
    }
}
