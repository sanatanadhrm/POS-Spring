package com.example.cashier.applications.dto.event;

import java.util.List;

public record OrderRestoreEvent(
        String storeId,
        List<RestoreItem> items) {
    public record RestoreItem(String productId, Integer qty) {
    }
}