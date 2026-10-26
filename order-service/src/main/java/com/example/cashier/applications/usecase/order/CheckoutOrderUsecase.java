package com.example.cashier.applications.usecase.order;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;
import com.example.cashier.applications.dto.request.order.CheckoutOrderRequest;
import com.example.cashier.applications.dto.request.product.DeductBatchItemProductRequest;
import com.example.cashier.applications.dto.request.product.DeductBatchProductRequest;
import com.example.cashier.applications.dto.request.product.GetBatchProductRequest;
import com.example.cashier.applications.dto.response.order.CheckoutOrderResponse;
import com.example.cashier.applications.dto.response.product.GetBatchProductResponse;
import com.example.cashier.applications.dto.response.productvariant.GetBatchProductVariantResponse;
import com.example.cashier.applications.dto.response.user.GetDetailUserResponse;
import com.example.cashier.applications.gateway.OrderEventPublisher;
import com.example.cashier.applications.gateway.ProductClient;
import com.example.cashier.applications.gateway.UserClient;
import com.example.cashier.applications.indempotency.IndempotencyManager;
import com.example.cashier.applications.mapper.OrderMapper;
import com.example.cashier.commons.exception.ConflictException;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.commons.exception.UnauthorizedException;
import com.example.cashier.domains.orders.OrderRepository;
import com.example.cashier.domains.orders.entity.Orders;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CheckoutOrderUsecase {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final IndempotencyManager idempotencyManager;
    private final OrderEventPublisher orderEventPublisher;

    public CheckoutOrderUsecase(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            UserClient userClient,
            ProductClient productClient,
            IndempotencyManager idempotencyManager,
            OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.userClient = userClient;
        this.productClient = productClient;
        this.idempotencyManager = idempotencyManager;
        this.orderEventPublisher = orderEventPublisher;
    }

    private GetDetailUserResponse validateUserRole(CheckoutOrderRequest payload, String username) {
        GetDetailUserResponse userResponse = this.userClient.getDetailUserByUsername(username);

        log.info("User response: {}", userResponse);

        if (!userResponse.getRoleName().equals("CASHIER")) {
            throw new UnauthorizedException("You are not allowed to create an order.");
        }

        return userResponse;
    }

    @Transactional
    public CheckoutOrderResponse execute(CheckoutOrderRequest payload, String username, String idempotencyKey) {
        boolean hasIdempotencyKey = idempotencyKey != null && !idempotencyKey.isBlank();

        // 1. Cek apakah response sudah pernah di-cache di Redis (Fast-path)
        if (hasIdempotencyKey) {
            CheckoutOrderResponse cachedResponse = this.idempotencyManager.getCachedResponse(idempotencyKey,
                    CheckoutOrderResponse.class);
            if (cachedResponse != null) {
                log.info("Idempotency match found in Redis! Returning cached response for key: {}", idempotencyKey);
                return cachedResponse;
            }

            // 2. Cek database PostgreSQL sebagai fallback jika Redis sempat restart/evict
            Optional<Orders> existingOrder = this.orderRepository.findByIdempotencyKey(idempotencyKey);
            if (existingOrder.isPresent()) {
                log.info("Idempotency match found in DB! Returning mapped response for key: {}", idempotencyKey);
                CheckoutOrderResponse response = this.orderMapper.mapOrdersToCheckoutOrderResponse(existingOrder.get());
                this.idempotencyManager.saveResponse(idempotencyKey, response, Duration.ofHours(24));
                return response;
            }

            // 3. Mengunci key menggunakan Redis SETNX (60 detik) untuk mencegah double
            // submit
            boolean lockAcquired = this.idempotencyManager.acquireLock(idempotencyKey, Duration.ofSeconds(60));
            if (!lockAcquired) {
                log.warn("Concurrent duplicate checkout request detected for key: {}", idempotencyKey);
                throw new ConflictException("This transaction is already being processed, please wait!");
            }
        }

        try {
            GetDetailUserResponse userResponse = validateUserRole(payload, username);
            // validatePayment(payload);

            String cashierRoleId = userResponse.getId();
            String storeId = userResponse.getStoreId();

            String orderId = UUID.randomUUID().toString();
            log.info("create orders");
            Orders orders = Orders.builder()
                    .id(orderId)
                    .storeId(storeId)
                    .cashierId(cashierRoleId)
                    .orderType(payload.getOrderType())
                    .tableNumber(payload.getTableNumber())
                    .cashReceived(payload.getCashReceive())
                    .paymentMethod(payload.getPaymentMethod())
                    .idempotencyKey(idempotencyKey)
                    .build();
            log.info("get batch product...");
            List<GetBatchProductResponse> getBatchProductResponse = this.productClient
                    .getBatchProduct(GetBatchProductRequest.builder().productIds(
                            payload.getItems().stream().map(item -> item.getProductId()).collect(Collectors.toList()))
                            .storeId(storeId).build());
            log.info("success get batch product");
            Map<String, GetBatchProductResponse> productMap = getBatchProductResponse.stream()
                    .collect(Collectors.toMap(product -> product.getId(), Function.identity()));

            log.info("looping items for validate");
            for (var itemReq : payload.getItems()) {
                GetBatchProductResponse product = productMap.get(itemReq.getProductId());

                if (product == null) {
                    throw new NotFoundException("Product not found!");
                }

                BigDecimal unitPrice = product.getPrice();

                if (itemReq.getProductVariantId() != null) {
                    if (product.getVariants().isEmpty()) {
                        throw new NotFoundException("Product variant not found!");
                    }
                    GetBatchProductVariantResponse variant = product.getVariants().stream()
                            .filter(v -> v.getId().equals(itemReq.getProductVariantId())).findFirst()
                            .orElseThrow(() -> new NotFoundException(
                                    "Varian ID " + itemReq.getProductVariantId() + " tidak ditemukan untuk produk "
                                            + product.getName()));
                    unitPrice = unitPrice.add(variant.getAdditionalPrice());
                }
                orders.addItem(itemReq.getProductId(), itemReq.getProductVariantId(), itemReq.getQty(), unitPrice);
            }
            orders.completeCheckout();

            List<DeductBatchItemProductRequest> deductItems = payload.getItems().stream()
                    .map(item -> new DeductBatchItemProductRequest(item.getProductId(), item.getQty(),
                            item.getProductVariantId()))
                    .toList();
            this.productClient.deductBatchProduct(DeductBatchProductRequest.builder()
                    .storeId(storeId)
                    .items(deductItems).build());

            Orders savedOrder = orderRepository.save(orders);

            CheckoutOrderResponse response = this.orderMapper.mapOrdersToCheckoutOrderResponse(savedOrder);

            // 4. Sukses: Simpan hasil response ke Redis selama 24 jam
            if (hasIdempotencyKey) {
                this.idempotencyManager.saveResponse(idempotencyKey, response, Duration.ofHours(24));
            }

            // 5. Asynchronous Event: Publikasikan OrderCreatedEvent ke message broker via Port Interface
            OrderCreatedEvent event = OrderCreatedEvent.builder()
                    .orderId(savedOrder.getId())
                    .storeId(savedOrder.getStoreId())
                    .cashierId(savedOrder.getCashierId())
                    .orderType(savedOrder.getOrderType() != null ? savedOrder.getOrderType().name() : null)
                    .paymentMethod(savedOrder.getPaymentMethod() != null ? savedOrder.getPaymentMethod().name() : null)
                    .tableNumber(savedOrder.getTableNumber())
                    .totalPrice(savedOrder.getTotalPrice())
                    .totalItems(savedOrder.getItems() != null ? savedOrder.getItems().size() : 0)
                    .createdAt(savedOrder.getCreatedAt() != null ? savedOrder.getCreatedAt() : java.time.LocalDateTime.now())
                    .build();
            this.orderEventPublisher.publishOrderCreated(event);

            return response;
        } catch (Exception e) {
            // Jika terjadi error, lepaskan kunci di Redis agar kasir bisa retry
            if (hasIdempotencyKey) {
                log.info("Checkout failed, releasing idempotency lock for key: {}", idempotencyKey);
                this.idempotencyManager.releaseLock(idempotencyKey);
            }
            throw e;
        }
    }
}
