package com.example.cashier.infrastructures.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.cashier.commons.properties.SecretProperties;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class RestApiConfig {

    private final SecretProperties secretProperties;

    public RestApiConfig(SecretProperties secretProperties) {
        this.secretProperties = secretProperties;
    }

    private ClientHttpRequestInterceptor commonHeaderInterceptor() {
        return (outgoingRequest, body, execution) -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest incomingRequest = attributes.getRequest();
                String authHeader = incomingRequest.getHeader("Authorization");
                if (authHeader != null) {
                    outgoingRequest.getHeaders().add("Authorization", authHeader);
                }
            }
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
                .build();
    }

}
