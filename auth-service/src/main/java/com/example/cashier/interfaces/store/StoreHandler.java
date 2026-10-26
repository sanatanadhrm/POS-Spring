package com.example.cashier.interfaces.store;

import org.springframework.web.bind.annotation.*;

import com.example.cashier.applications.dto.response.store.GetStoreByIdResponse;
import com.example.cashier.applications.usecase.store.GetStoreByIdUseCase;
import com.example.cashier.commons.dto.WebResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/stores")
@Slf4j
public class StoreHandler {

    private final GetStoreByIdUseCase getStoreByIdUseCase;

    public StoreHandler(GetStoreByIdUseCase getStoreByIdUseCase) {
        this.getStoreByIdUseCase = getStoreByIdUseCase;
    }

    @GetMapping("/{storeId}")
    public WebResponse<GetStoreByIdResponse> getStoreById(@PathVariable String storeId) {
        GetStoreByIdResponse response = getStoreByIdUseCase.execute(storeId);
        return WebResponse.<GetStoreByIdResponse>builder()
                .status("Success")
                .message("Store retrieved successfully")
                .data(response)
                .build();
    }
}
