package com.example.cashier.infrastructures.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.cashier.commons.properties.SecretProperties;

import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class RestApiConfig {

    private final SecretProperties secretProperties;
    private final ObservationRegistry observationRegistry;

    public RestApiConfig(SecretProperties secretProperties, ObservationRegistry observationRegistry) {
        this.secretProperties = secretProperties;
        this.observationRegistry = observationRegistry;
    }

    // 1. Buat Interceptor yang Reusable untuk SEMUA RestClient
    private ClientHttpRequestInterceptor commonHeaderInterceptor() {
        return (outgoingRequest, body, execution) -> {
            // A. Ambil Token Authorization (jika sedang berada dalam konteks HTTP Request)
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest incomingRequest = attributes.getRequest();
                String authHeader = incomingRequest.getHeader("Authorization");
                if (authHeader != null) {
                    outgoingRequest.getHeaders().add("Authorization", authHeader);
                }
            }
            // B. Ambil Correlation ID langsung dari MDC (tetap jalan meski attributes null)
            String correlationId = MDC.get("correlationId");
            if (correlationId != null) {
                outgoingRequest.getHeaders().add("X-Correlation-ID", correlationId);
            }
            return execution.execute(outgoingRequest, body);
        };
    }

    @Bean
    public RestClient authRestClient() {
        return RestClient.builder()
                .baseUrl(this.secretProperties.getAuthServiceUrl())
                .requestInterceptor(commonHeaderInterceptor())
                .observationRegistry(this.observationRegistry)
                .build();
    }

    @Bean
    public RestClient productRestClient() {
        return RestClient.builder()
                .baseUrl(this.secretProperties.getProductServiceUrl())
                .requestInterceptor(commonHeaderInterceptor())
                .observationRegistry(this.observationRegistry)
                .build();
    }

}
