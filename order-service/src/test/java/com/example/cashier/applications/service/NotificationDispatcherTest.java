package com.example.cashier.applications.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.cashier.applications.dto.notification.NotificationPayload;
import com.example.cashier.applications.dto.notification.NotificationType;
import com.example.cashier.applications.port.output.NotificationChannelSender;

@ExtendWith(MockitoExtension.class)
class NotificationDispatcherTest {

    @Mock
    private NotificationChannelSender emailSender;

    @Mock
    private NotificationChannelSender waSender;

    private NotificationPayload<String> samplePayload;

    @BeforeEach
    void setUp() {
        samplePayload = NotificationPayload.<String>builder()
                .recipientName("Budi")
                .phoneNumber("08123456789")
                .recipientEmail("budi@example.com")
                .subject("Struk Pembelian #1")
                .type(NotificationType.RECEIPT)
                .data("Detail Order")
                .attachment(new byte[]{1, 2, 3})
                .attachmentFileName("struk-1.pdf")
                .build();
    }

    @Test
    @DisplayName("Skenario 1: Sukses - Semua channel valid terpanggil dan selesai dieksekusi")
    void shouldDispatchToAllApplicableSenders() {
        // GIVEN: Kedua channel applicable dan selesai sukses
        when(emailSender.isApplicable(samplePayload)).thenReturn(true);
        when(emailSender.sendAsync(samplePayload)).thenReturn(CompletableFuture.completedFuture(null));

        when(waSender.isApplicable(samplePayload)).thenReturn(true);
        when(waSender.sendAsync(samplePayload)).thenReturn(CompletableFuture.completedFuture(null));

        NotificationDispatcher dispatcher = new NotificationDispatcher(List.of(emailSender, waSender));

        // WHEN & THEN: Dispatch tidak boleh melempar exception
        assertDoesNotThrow(() -> dispatcher.dispatch(samplePayload));

        // Verifikasi kedua channel dieksekusi
        verify(emailSender, times(1)).sendAsync(samplePayload);
        verify(waSender, times(1)).sendAsync(samplePayload);
    }

    @Test
    @DisplayName("Skenario 2: Filter - Hanya channel yang applicable yang dikirimkan")
    void shouldDispatchOnlyToApplicableSenders() {
        // GIVEN: Email applicable, tapi WA tidak applicable (misal karena nomor HP kosong)
        when(emailSender.isApplicable(samplePayload)).thenReturn(true);
        when(emailSender.sendAsync(samplePayload)).thenReturn(CompletableFuture.completedFuture(null));

        when(waSender.isApplicable(samplePayload)).thenReturn(false);

        NotificationDispatcher dispatcher = new NotificationDispatcher(List.of(emailSender, waSender));

        // WHEN
        dispatcher.dispatch(samplePayload);

        // THEN
        verify(emailSender, times(1)).sendAsync(samplePayload);
        verify(waSender, never()).sendAsync(any());
    }

    @Test
    @DisplayName("Skenario 3A: Gagal - Lempar RuntimeException jika tidak ada channel yang applicable")
    void shouldThrowExceptionWhenNoSendersAreApplicable() {
        // GIVEN: Semua sender mengembalikan false
        when(emailSender.isApplicable(samplePayload)).thenReturn(false);
        when(waSender.isApplicable(samplePayload)).thenReturn(false);

        NotificationDispatcher dispatcher = new NotificationDispatcher(List.of(emailSender, waSender));

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> dispatcher.dispatch(samplePayload));
        assertEquals("Tidak ada channel notifikasi yang valid", exception.getMessage());

        verify(emailSender, never()).sendAsync(any());
        verify(waSender, never()).sendAsync(any());
    }

    @Test
    @DisplayName("Skenario 3B: Gagal - Lempar RuntimeException jika daftar channelSenders kosong")
    void shouldThrowExceptionWhenSenderListIsEmpty() {
        // GIVEN: List sender kosong
        NotificationDispatcher dispatcher = new NotificationDispatcher(Collections.emptyList());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> dispatcher.dispatch(samplePayload));
        assertEquals("Tidak ada channel notifikasi yang valid", exception.getMessage());
    }

    @Test
    @DisplayName("Skenario 4: Gagal Async - Lempar CompletionException jika salah satu pengiriman async error")
    void shouldThrowCompletionExceptionWhenAnySenderFailsAsync() {
        // GIVEN: Email sukses, tetapi WA gagal secara asynchronous
        when(emailSender.isApplicable(samplePayload)).thenReturn(true);
        when(emailSender.sendAsync(samplePayload)).thenReturn(CompletableFuture.completedFuture(null));

        when(waSender.isApplicable(samplePayload)).thenReturn(true);
        when(waSender.sendAsync(samplePayload)).thenReturn(
                CompletableFuture.failedFuture(new RuntimeException("WhatsApp Gateway Timeout"))
        );

        NotificationDispatcher dispatcher = new NotificationDispatcher(List.of(emailSender, waSender));

        // WHEN & THEN: CompletableFuture.allOf().join() melempar CompletionException yang membungkus error aslinya
        CompletionException exception = assertThrows(CompletionException.class, () -> dispatcher.dispatch(samplePayload));
        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertEquals("WhatsApp Gateway Timeout", exception.getCause().getMessage());

        verify(emailSender, times(1)).sendAsync(samplePayload);
        verify(waSender, times(1)).sendAsync(samplePayload);
    }
}
