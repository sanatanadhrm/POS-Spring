package com.example.cashier.applications.port.output;

import com.example.cashier.applications.dto.event.OrderCreatedEvent;

public interface ReceiptGeneratorPort {
    byte[] generateReceipt(OrderCreatedEvent data);

}
