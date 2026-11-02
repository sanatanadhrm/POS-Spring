package com.example.cashier.interfaces.http.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.cashier.applications.dto.request.order.CheckoutOrderRequest;
import com.example.cashier.applications.dto.request.orderitem.CheckoutOrderItemRequest;
import com.example.cashier.applications.dto.response.order.CheckoutOrderResponse;
import com.example.cashier.applications.usecase.order.CheckoutOrderUsecase;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.domains.orders.entity.OrderPaymentMethod;
import com.example.cashier.domains.orders.entity.OrderType;
import com.example.cashier.infrastructures.security.JwtTokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = OrderHandler.class)
@AutoConfigureMockMvc(addFilters = false) // Fokus menguji layer Web/HTTP, validasi DTO, dan exception handling
class OrderHandlerTest {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @MockitoBean
        private CheckoutOrderUsecase checkoutOrderUsecase;

        @MockitoBean
        private JwtTokenManager jwtTokenManager;

        private CheckoutOrderRequest validRequest;
        private CheckoutOrderResponse mockResponse;

        @BeforeEach
        void setUp() {
                CheckoutOrderItemRequest item = CheckoutOrderItemRequest.builder()
                                .productId("prod-1")
                                .qty(2)
                                .build();

                validRequest = CheckoutOrderRequest.builder()
                                .paymentMethod(OrderPaymentMethod.CASH)
                                .orderType(OrderType.DINE_IN)
                                .tableNumber("12")
                                .cashReceive(BigDecimal.valueOf(100000))
                                .items(List.of(item))
                                .build();

                mockResponse = CheckoutOrderResponse.builder()
                                .id("order-123")
                                .totalPrice(BigDecimal.valueOf(50000))
                                .cashReceived(BigDecimal.valueOf(100000))
                                .changeAmount(BigDecimal.valueOf(50000))
                                .build();
        }

        @Test
        @DisplayName("1. Happy Path - Berhasil checkout ketika header dan body valid")
        void shouldReturn200WhenRequestIsValid() throws Exception {
                when(checkoutOrderUsecase.execute(any(), any(), eq("id-key-123")))
                                .thenReturn(mockResponse);

                mockMvc.perform(
                                post("/api/v1/orders")
                                                .header("X-Idempotency-Key", "id-key-123")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value("Success"))
                                .andExpect(jsonPath("$.message").value("Order created successfully"));
                verify(checkoutOrderUsecase, atLeastOnce()).execute(any(), any(), eq("id-key-123"));
        }

        @Test
        @DisplayName("2. Header Validation - Error 400 ketika header X-Idempotency-Key tidak disertakan")
        void shouldReturn400WhenIdempotencyKeyHeaderIsMissing() throws Exception {
                mockMvc.perform(post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value("Error"))
                                .andExpect(jsonPath("$.message").value("Header 'X-Idempotency-Key' is required!"));

                verify(checkoutOrderUsecase, never()).execute(any(), any(), any());
        }

        @Test
        @DisplayName("3. Header Validation - Error 400 ketika header X-Idempotency-Key berupa string kosong / spasi")
        void shouldReturn400WhenIdempotencyKeyIsBlank() throws Exception {
                mockMvc.perform(post("/api/v1/orders")
                                .header("X-Idempotency-Key", "   ")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value("Error"))
                                .andExpect(jsonPath("$.message").value("Header 'X-Idempotency-Key' is required!"));

                verify(checkoutOrderUsecase, never()).execute(any(), any(), any());
        }

        @Test
        @DisplayName("4. DTO Validation - Error 400 ketika items pesanan kosong")
        void shouldReturn400WhenItemsListIsEmpty() throws Exception {
                validRequest.setItems(List.of()); // Melanggar @NotEmpty

                mockMvc.perform(post("/api/v1/orders")
                                .header("X-Idempotency-Key", "idemp-key-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value("Error"))
                                .andExpect(jsonPath("$.message").value("items: Items pesanan tidak boleh kosong"));

                verify(checkoutOrderUsecase, never()).execute(any(), any(), any());
        }

        @Test
        @DisplayName("5. DTO Validation - Error 400 ketika cashReceive bernilai negatif")
        void shouldReturn400WhenCashReceiveIsNegative() throws Exception {
                validRequest.setCashReceive(BigDecimal.valueOf(-5000)); // Melanggar @PositiveOrZero

                mockMvc.perform(post("/api/v1/orders")
                                .header("X-Idempotency-Key", "idemp-key-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value("Error"))
                                .andExpect(jsonPath("$.message")
                                                .value("cashReceive: Cash received tidak boleh negatif"));

                verify(checkoutOrderUsecase, never()).execute(any(), any(), any());
        }

        @Test
        @DisplayName("6. Business Exception Mapping - Error 404 ketika use case melempar NotFoundException")
        void shouldReturn404WhenProductNotFound() throws Exception {
                when(checkoutOrderUsecase.execute(any(), any(), eq("idemp-key-123")))
                                .thenThrow(new NotFoundException("Produk dengan ID prod-1 tidak ditemukan"));

                mockMvc.perform(post("/api/v1/orders")
                                .header("X-Idempotency-Key", "idemp-key-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value("Error"))
                                .andExpect(jsonPath("$.message").value("Produk dengan ID prod-1 tidak ditemukan"));
        }
}
