package com.example.cashier.applications.usecase.order;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;
import com.example.cashier.applications.dto.request.order.CheckoutOrderRequest;
import com.example.cashier.applications.dto.request.orderitem.CheckoutOrderItemRequest;
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
import com.example.cashier.domains.orders.entity.OrderPaymentMethod;
import com.example.cashier.domains.orders.entity.OrderType;
import com.example.cashier.domains.orders.entity.Orders;

/**
 * Unit Test untuk CheckoutOrderUsecase.
 * Menggunakan MockitoExtension agar test berjalan cepat di memori (tanpa memuat
 * Spring Boot context).
 */
@ExtendWith(MockitoExtension.class)
class CheckoutOrderUsecaseTest {

        // --- 1. MOCK DEPENDENCIES (Objek Tiruan / Pengganti Service Luar) ---
        @Mock
        private OrderRepository orderRepository;

        @Mock
        private OrderMapper orderMapper;

        @Mock
        private UserClient userClient;

        @Mock
        private ProductClient productClient;

        @Mock
        private IndempotencyManager idempotencyManager;

        @Mock
        private OrderEventPublisher orderEventPublisher;

        // --- 2. TARGET CLASS YANG DIUJI ---
        // Mockito otomatis menyuntikkan seluruh @Mock di atas ke dalam constructor
        // CheckoutOrderUsecase
        @InjectMocks
        private CheckoutOrderUsecase checkoutOrderUsecase;

        // --- Variabel Data Dummy Bersama ---
        private CheckoutOrderRequest sampleRequest;
        private GetDetailUserResponse sampleCashierUser;
        private GetDetailUserResponse sampleAdminUser;
        private GetBatchProductResponse sampleProduct;
        private final String sampleIdempotencyKey = "sample-idempotency-key-uuid";
        private final String sampleCashierUsername = "kasir_budi";
        private final String sampleAdminUsername = "admin";

        @BeforeEach
        void setUp() {
                // Persiapan data dummy sebelum setiap method @Test dijalankan
                sampleCashierUser = new GetDetailUserResponse();
                sampleCashierUser.setId("user-cashier-001");
                sampleCashierUser.setUsername(sampleCashierUsername);
                sampleCashierUser.setRoleName("CASHIER");
                sampleCashierUser.setStoreId("store-central-01");

                sampleAdminUser = new GetDetailUserResponse();
                sampleAdminUser.setId("user-admin-001");
                sampleAdminUser.setUsername(sampleAdminUsername);
                sampleAdminUser.setRoleName("ADMIN");
                sampleAdminUser.setStoreId("store-central-01");

                GetBatchProductVariantResponse sampleVariant = GetBatchProductVariantResponse.builder()
                                .id("variant-prod-101")
                                .name("Less Sugar")
                                .additionalPrice(BigDecimal.valueOf(5000))
                                .build();

                sampleProduct = GetBatchProductResponse.builder()
                                .id("prod-101")
                                .name("Kopi Susu Gula Aren")
                                .price(BigDecimal.valueOf(20000))
                                .qty(50)
                                .storeId("store-central-01")
                                .variants(List.of(sampleVariant))
                                .build();

                CheckoutOrderItemRequest itemRequest = CheckoutOrderItemRequest.builder()
                                .productId("prod-101")
                                .productVariantId("variant-prod-101")
                                .qty(2)
                                .build();

                sampleRequest = CheckoutOrderRequest.builder()
                                .orderType(OrderType.DINE_IN)
                                .tableNumber("A-01")
                                .paymentMethod(OrderPaymentMethod.CASH)
                                .cashReceive(BigDecimal.valueOf(50000))
                                .items(List.of(itemRequest))
                                .build();
        }

        // =========================================================================
        // SKENARIO 1: HAPPY PATH (Checkout Sukses Pertama Kali)
        // =========================================================================
        @Test
        @DisplayName("Skenario 1: Checkout sukses ketika kasir valid, produk tersedia, dan key belum pernah dipakai")
        void shouldSuccessfullyCheckoutOrder_WhenDataIsValid() {
                // --- A: ARRANGE (Persiapan data dan perilaku mock) ---
                // 1. Simulasi Idempotency: Belum ada di cache Redis dan belum ada di DB
                when(idempotencyManager.getCachedResponse(sampleIdempotencyKey, CheckoutOrderResponse.class))
                                .thenReturn(null);
                when(orderRepository.findByIdempotencyKey(sampleIdempotencyKey))
                                .thenReturn(Optional.empty());
                // 2. Simulasi berhasil mendapatkan lock di Redis
                when(idempotencyManager.acquireLock(eq(sampleIdempotencyKey), any(Duration.class)))
                                .thenReturn(true);

                // 3. Simulasi respon gateway user dan produk
                when(userClient.getDetailUserByUsername(sampleCashierUsername))
                                .thenReturn(sampleCashierUser);
                when(productClient.getBatchProduct(any()))
                                .thenReturn(List.of(sampleProduct));

                // 4. Simulasi penyimpanan ke database order
                when(orderRepository.save(any(Orders.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // 5. Simulasi mapping ke DTO response
                CheckoutOrderResponse expectedResponse = CheckoutOrderResponse.builder()
                                .id("order-generated-id")
                                .cashierId(sampleCashierUser.getId())
                                .tableNumber("A-01")
                                .build();
                when(orderMapper.mapOrdersToCheckoutOrderResponse(any(Orders.class)))
                                .thenReturn(expectedResponse);

                // --- A: ACT (Eksekusi fungsi yang diuji) ---
                CheckoutOrderResponse actualResponse = checkoutOrderUsecase.execute(
                                sampleRequest,
                                sampleCashierUsername,
                                sampleIdempotencyKey);

                // --- A: ASSERT (Pemeriksaan hasil) ---
                assertNotNull(actualResponse);
                assertEquals("order-generated-id", actualResponse.getId());
                assertEquals("user-cashier-001", actualResponse.getCashierId());

                // Verifikasi penting: Pastikan repository menyimpan data ke DB tepat 1 kali
                verify(orderRepository, times(1)).save(any(Orders.class));

                // Verifikasi penting: Pastikan stok dipotong tepat 1 kali via Feign/RestClient
                verify(productClient, times(1)).deductBatchProduct(any());

                // Verifikasi penting: Pastikan response sukses disimpan ke cache Redis (TTL 24
                // jam)
                verify(idempotencyManager, times(1)).saveResponse(eq(sampleIdempotencyKey), eq(expectedResponse),
                                any(Duration.class));

                // Verifikasi penting: Pastikan releaseLock TIDAK PERNAH dipanggil karena
                // transaksi berhasil
                verify(idempotencyManager, never()).releaseLock(any());

                // Verifikasi penting: Pastikan event order.created dipublikasikan ke broker tepat 1 kali
                verify(orderEventPublisher, times(1)).publishOrderCreated(any(OrderCreatedEvent.class));
        }

        // =========================================================================
        // TODO SKENARIO 2: Idempotency Cache Hit (Request ke-2 Mengembalikan Cache)
        // =========================================================================

        @Test
        @DisplayName("Skenario 2: Mengembalikan response dari cache jika idempotency key sudah pernah ada")
        void shouldReturnCachedResponse_WhenIdempotencyKeyExists() {

                CheckoutOrderResponse sampleResponse = CheckoutOrderResponse.builder()
                                .id("order-generated-id")
                                .cashierId(sampleCashierUser.getId())
                                .tableNumber("A-01")
                                .build();

                when(idempotencyManager.getCachedResponse(sampleIdempotencyKey, CheckoutOrderResponse.class))
                                .thenReturn(sampleResponse);

                CheckoutOrderResponse actualResponse = checkoutOrderUsecase.execute(
                                sampleRequest,
                                sampleCashierUsername,
                                sampleIdempotencyKey);

                assertNotNull(actualResponse);
                assertEquals(sampleResponse.getId(), actualResponse.getId());

                verify(idempotencyManager, times(1)).getCachedResponse(eq(sampleIdempotencyKey),
                                eq(CheckoutOrderResponse.class));
                verify(idempotencyManager, never()).saveResponse(any(), any(), any());
                verify(idempotencyManager, never()).acquireLock(any(), any(Duration.class));
                verify(idempotencyManager, never()).releaseLock(any());
                verify(orderRepository, never()).save(any());
                verify(productClient, never()).deductBatchProduct(any());
                verify(orderEventPublisher, never()).publishOrderCreated(any());
        }

        // =========================================================================
        // TODO SKENARIO 3: Concurrent Lock Conflict (Kasir Double Click Cepat)
        // =========================================================================
        @Test
        @DisplayName("Skenario 3: Gagal checkout dan merilis lock jika user lain sudah memproses data yang sama")
        void shouldThrowConflictException_WhenDuplicateCheckoutDetected() {

                when(idempotencyManager.getCachedResponse(any(), any())).thenReturn(null);
                when(idempotencyManager.acquireLock(any(), any())).thenReturn(false);

                ConflictException exception = assertThrows(ConflictException.class, () -> {
                        checkoutOrderUsecase.execute(sampleRequest, sampleCashierUsername,
                                        sampleIdempotencyKey);
                });

                assertNotNull(exception);
                assertEquals("This transaction is already being processed, please wait!", exception.getMessage());

                verify(idempotencyManager, never()).saveResponse(any(), any(), any());
                verify(idempotencyManager, times(1)).acquireLock(any(), any(Duration.class));
                verify(idempotencyManager, never()).releaseLock(any());
                verify(orderRepository, never()).save(any());
                verify(productClient, never()).deductBatchProduct(any());
                verify(orderEventPublisher, never()).publishOrderCreated(any());

        }

        // =========================================================================
        // TODO SKENARIO 4: Security Role Violation (User Bukan CASHIER)
        // =========================================================================

        @Test
        @DisplayName("Skenario 4: Gagal checkout ketika user bukan CASHIER")
        void shouldThrowUnauthorizedException_WhenUserIsNotCashier() {

                when(idempotencyManager.getCachedResponse(sampleIdempotencyKey, CheckoutOrderResponse.class))
                                .thenReturn(null);
                when(orderRepository.findByIdempotencyKey(sampleIdempotencyKey)).thenReturn(Optional.empty());
                when(idempotencyManager.acquireLock(any(), any())).thenReturn(true);

                when(userClient.getDetailUserByUsername(sampleAdminUsername))
                                .thenReturn(sampleAdminUser);

                UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
                        checkoutOrderUsecase.execute(sampleRequest, sampleAdminUsername, sampleIdempotencyKey);
                });

                assertNotNull(exception);
                assertEquals("You are not allowed to create an order.", exception.getMessage());

                verify(idempotencyManager, never()).saveResponse(any(), any(), any());
                verify(idempotencyManager, times(1)).acquireLock(any(), any(Duration.class));
                verify(idempotencyManager, times(1)).releaseLock(any());
                verify(orderRepository, never()).save(any());
                verify(productClient, never()).deductBatchProduct(any());
                verify(orderEventPublisher, never()).publishOrderCreated(any());

        }

        // =========================================================================
        // TODO SKENARIO 5: Product Not Found (Stok/Produk Tidak Ada)
        // =========================================================================
        @Test
        @DisplayName("Skenario 5: Gagal checkout ketika produk tidak ditemukan")
        void shouldThrowNotFoundException_WhenProductNotFound() {

                when(idempotencyManager.getCachedResponse(sampleIdempotencyKey, CheckoutOrderResponse.class))
                                .thenReturn(null);
                when(orderRepository.findByIdempotencyKey(sampleIdempotencyKey)).thenReturn(Optional.empty());
                when(idempotencyManager.acquireLock(any(), any())).thenReturn(true);

                when(userClient.getDetailUserByUsername(sampleCashierUsername))
                                .thenReturn(sampleCashierUser);
                when(productClient.getBatchProduct(any())).thenReturn(List.of());
                NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                        checkoutOrderUsecase.execute(sampleRequest, sampleCashierUsername, sampleIdempotencyKey);
                });

                assertNotNull(exception);
                assertEquals("Product not found!", exception.getMessage());

                verify(idempotencyManager, never()).saveResponse(any(), any(), any());
                verify(idempotencyManager, times(1)).acquireLock(any(), any(Duration.class));
                verify(idempotencyManager, times(1)).releaseLock(any());
                verify(productClient, times(1)).getBatchProduct(any());
                verify(orderRepository, never()).save(any());
                verify(productClient, never()).deductBatchProduct(any());
                verify(orderEventPublisher, never()).publishOrderCreated(any());

        }

        // =========================================================================
        // TODO SKENARIO 6: Checkout Product dengan Product Variant yang tidak ditemukan
        // =========================================================================

        @Test
        @DisplayName("Skenario 6: Gagal checkout ketika product variant tidak ditemukan")
        void shouldThrowNotFoundException_WhenProductVariantNotFound() {

                when(idempotencyManager.getCachedResponse(sampleIdempotencyKey, CheckoutOrderResponse.class))
                                .thenReturn(null);
                when(orderRepository.findByIdempotencyKey(sampleIdempotencyKey)).thenReturn(Optional.empty());
                when(idempotencyManager.acquireLock(any(), any())).thenReturn(true);

                when(userClient.getDetailUserByUsername(sampleCashierUsername))
                                .thenReturn(sampleCashierUser);

                GetBatchProductResponse productTanpaVarian = GetBatchProductResponse.builder()
                                .id("prod-101")
                                .name("Kopi Susu Gula Aren")
                                .price(BigDecimal.valueOf(20000))
                                .qty(50)
                                .storeId("store-central-01")
                                .variants(List.of()) // <-- Kosong! Agar memicu "Product variant not found!"
                                .build();
                when(productClient.getBatchProduct(any())).thenReturn(List.of(productTanpaVarian));

                NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                        checkoutOrderUsecase.execute(sampleRequest, sampleCashierUsername, sampleIdempotencyKey);
                });

                assertNotNull(exception);
                assertEquals("Product variant not found!", exception.getMessage());

                verify(idempotencyManager, never()).saveResponse(any(), any(), any());
                verify(idempotencyManager, times(1)).acquireLock(any(), any(Duration.class));
                verify(idempotencyManager, times(1)).releaseLock(any());
                verify(productClient, times(1)).getBatchProduct(any());
                verify(orderRepository, never()).save(any());
                verify(productClient, never()).deductBatchProduct(any());
                verify(orderEventPublisher, never()).publishOrderCreated(any());

        }
}
