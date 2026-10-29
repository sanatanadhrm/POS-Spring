package com.example.cashier.infrastructures.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.trace.export.SpanExporter;

@Configuration
public class TracingConfig {

    @Bean
    public SpanExporter otlpHttpSpanExporter(
            @Value("${management.otlp.tracing.endpoint:http://tempo:4318/v1/traces}") String endpoint) {
        return OtlpHttpSpanExporter.builder()
                .setEndpoint(endpoint)
                .build();
    }
}
