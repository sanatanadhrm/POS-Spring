package com.example.cashier.interfaces.order;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cashier.applications.dto.request.order.CheckoutOrderRequest;
import com.example.cashier.applications.dto.response.order.CheckoutOrderResponse;
import com.example.cashier.applications.usecase.order.CheckoutOrderUsecase;
import com.example.cashier.commons.dto.WebResponse;
import com.example.cashier.commons.exception.BadRequestException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderHandler {

    private final CheckoutOrderUsecase checkoutOrderUsecase;

    public OrderHandler(CheckoutOrderUsecase checkoutOrderUsecase) {
        this.checkoutOrderUsecase = checkoutOrderUsecase;
    }

    @PostMapping({ "", "/" })
    public WebResponse<CheckoutOrderResponse> checkout(
            @AuthenticationPrincipal String cashierId,
            @RequestHeader(value = "X-Idempotency-Key", required = true) String idempotencyKey,
            @Valid @RequestBody CheckoutOrderRequest request) {
        if (idempotencyKey.isBlank()) {
            throw new BadRequestException("Header 'X-Idempotency-Key' is required!");
        }
        CheckoutOrderResponse response = this.checkoutOrderUsecase.execute(request, cashierId, idempotencyKey);

        return WebResponse.<CheckoutOrderResponse>builder()
                .status("Success")
                .message("Order created successfully")
                .data(response)
                .build();
    }
}
