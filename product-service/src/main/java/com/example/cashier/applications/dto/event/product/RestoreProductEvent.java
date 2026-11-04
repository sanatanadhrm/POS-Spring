package com.example.cashier.applications.dto.event.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestoreProductEvent {
    private String storeId;
    private List<RestoreProductItemEvent> items;
}
