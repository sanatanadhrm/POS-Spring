package com.example.cashier.applications.port.output;

import com.example.cashier.applications.dto.notification.receipt.ReceiptData;

public interface ReceiptGeneratorPort {
    byte[] generateReceipt(ReceiptData data);

}
