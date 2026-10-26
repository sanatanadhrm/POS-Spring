package com.example.cashier.applications.usecase.store;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.response.store.GetStoreByIdResponse;
import com.example.cashier.commons.exception.NotFoundException;
import com.example.cashier.domains.store.StoreRepository;
import com.example.cashier.domains.store.entity.Store;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GetStoreByIdUseCase {

    private final StoreRepository storeRepository;

    public GetStoreByIdUseCase(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public GetStoreByIdResponse execute(String storeId) {
        log.info("masuk store usecase");
        Store store = storeRepository.getByStoreId(storeId);
        log.info("storeId verify successfully");

        if (store == null) {
            log.error("Store with id {} is not found!", storeId);
            throw new NotFoundException("Store not found");
        }

        return GetStoreByIdResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .build();
    }

}
