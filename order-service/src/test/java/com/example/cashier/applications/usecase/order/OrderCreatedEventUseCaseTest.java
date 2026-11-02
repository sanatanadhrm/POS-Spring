package com.example.cashier.applications.usecase.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;
import com.example.cashier.applications.dto.event.OrderCreatedEventItem;
import com.example.cashier.applications.port.output.ReceiptGeneratorPort;
import com.example.cashier.applications.service.NotificationDispatcher;

@ExtendWith(MockitoExtension.class)
public class OrderCreatedEventUseCaseTest {

    @Mock
    private NotificationDispatcher notificationDispatcher;

    @Mock
    private ReceiptGeneratorPort receiptGeneratorPort;

    @InjectMocks
    private OrderCreatedEventUseCase orderCreatedEventUseCase;

    private OrderCreatedEvent sampleEvent;

    @BeforeEach
    public void setUp() {
        sampleEvent = OrderCreatedEvent.builder()
                .orderId("1")
                .storeName("Warung Babeh")
                .cashierName("BUDI")
                .paymentMethod("CASH")
                .orderType("DINE_IN")
                .totalPrice(BigDecimal.valueOf(100000))
                .tableNumber("123")
                .cashReceive(BigDecimal.valueOf(100000))
                .change(BigDecimal.valueOf(100000))
                .totalItems(1)
                .items(List.of(OrderCreatedEventItem.builder()
                        .productName("Babeh")
                        .qty(1)
                        .unitPrice(BigDecimal.valueOf(100000))
                        .subtotal(BigDecimal.valueOf(100000))
                        .build()))
                .build();

    }

    @Test
    @DisplayName("Skenario 1: Sukses - PDF Terbuat dan Notifikasi Terkirim")
    void shouldSuccessWhenPdfAndDispatchSucceed() {
        // GIVEN: Generator PDF berhasil menghasilkan byte array
        when(receiptGeneratorPort.generateReceipt(any())).thenReturn(new byte[] { 1, 2, 3 });
        // WHEN & THEN: Tidak boleh melempar exception
        assertDoesNotThrow(() -> orderCreatedEventUseCase.execute(sampleEvent));
        // Verifikasi bahwa kedua port terpanggil
        verify(receiptGeneratorPort).generateReceipt(any());
        verify(notificationDispatcher).dispatch(any());
    }

    @Test
    @DisplayName("Skenario 2: Gagal - Generator PDF Melempar Error")
    void shouldThrowExceptionWhenPdfGenerationFails() {
        // GIVEN: Generator PDF simulasi crash/error
        when(receiptGeneratorPort.generateReceipt(any()))
                .thenThrow(new RuntimeException("Memori penuh saat render font PDF"));
        // WHEN & THEN: Use case harus melempar RuntimeException
        assertThrows(RuntimeException.class, () -> orderCreatedEventUseCase.execute(sampleEvent));
        // Verifikasi: Karena PDF gagal, jangan pernah panggil dispatcher!
        verify(notificationDispatcher, never()).dispatch(any());
    }

    @Test
    @DisplayName("Skenario 3: Gagal - Dispatcher Notifikasi Gagal Terkirim")
    void shouldThrowExceptionWhenNotificationDispatchFails() {
        // GIVEN: PDF sukses, tapi Dispatcher (WA/Email) error
        when(receiptGeneratorPort.generateReceipt(any())).thenReturn(new byte[] { 1, 2, 3 });
        doThrow(new RuntimeException("SMTP Connection Timeout"))
                .when(notificationDispatcher).dispatch(any());
        // WHEN & THEN: Use case harus melempar exception agar sistem tahu ada kegagalan
        assertThrows(RuntimeException.class, () -> orderCreatedEventUseCase.execute(sampleEvent));
    }
}
