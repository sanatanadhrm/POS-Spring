package com.example.cashier.infrastructures.gateway.product;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.cashier.applications.dto.request.product.DeductBatchProductRequest;
import com.example.cashier.applications.dto.request.product.GetBatchProductRequest;
import com.example.cashier.applications.dto.response.product.GetBatchProductResponse;
import com.example.cashier.applications.gateway.ProductClient;
import com.example.cashier.commons.dto.WebResponse;
import com.example.cashier.commons.exception.BadRequestException;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.commons.exception.UnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProductHttpGateway implements ProductClient {

    private final RestClient productRestClient;

    public ProductHttpGateway(RestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "getBatchProductFallback")
    public List<GetBatchProductResponse> getBatchProduct(GetBatchProductRequest request) {
        log.info("hit api get batch : {}", request.getProductIds());
        WebResponse<List<GetBatchProductResponse>> response = this.productRestClient.post()
                .uri(builder -> builder.path("/products/batch").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 400, (req, res) -> {
                    throw new BadRequestException("Invalid request!");
                })
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new NotFoundException("Product is not found!");
                })
                .onStatus(status -> status.value() == 401 || status.value() == 403, (req, res) -> {
                    throw new UnauthorizedException(
                            "Akses ditolak: Token autentikasi tidak valid atau sudah kadaluarsa!");
                })
                .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                    throw new RuntimeException("Failed to contact Auth Service. Please try again.");
                })
                .body(new ParameterizedTypeReference<WebResponse<List<GetBatchProductResponse>>>() {
                });

        return response.getData();
    }

    public List<GetBatchProductResponse> getBatchProductFallback(GetBatchProductRequest request, Throwable ex) {
        log.error("Product Service is DOWN/BUSY! Triggering fallback. Reason: {}", ex.getMessage());
        throw new UnavailableException("Layanan Produk sedang tidak dapat diakses, silakan coba sesaat lagi.");

    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "deductBatchProductFallback")
    public void deductBatchProduct(DeductBatchProductRequest request) {
        log.info("hit api deduct batch : {}", request.getItems());
        this.productRestClient.put()
                .uri(builder -> builder.path("/products/batch-deduct").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 200 || status.value() == 201, (req, res) -> {
                    log.info("success deduct batch");
                })
                .onStatus(status -> status.value() == 400, (req, res) -> {
                    throw new BadRequestException("Invalid request!");
                })
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new NotFoundException("Product is not found!");
                })
                .onStatus(status -> status.value() == 401 || status.value() == 403, (req, res) -> {
                    throw new UnauthorizedException(
                            "Akses ditolak: Token autentikasi tidak valid atau sudah kadaluarsa!");
                })
                .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                    throw new RuntimeException("Failed to contact Auth Service. Please try again.");
                })
                .body(Void.class);

    }

    public void deductBatchProductFallback(DeductBatchProductRequest request, Throwable ex) {
        log.error("Product Service is DOWN/BUSY! Triggering fallback. Reason: {}", ex.getMessage());
        throw new UnavailableException("Layanan Produk sedang tidak dapat diakses, silakan coba sesaat lagi.");
    }

}
