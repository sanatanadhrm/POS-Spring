package com.example.cashier.domains.store;

import com.example.cashier.domains.store.entity.Store;

public interface StoreRepository {
    Store getByStoreId(String storeId);

    void save(Store store);

}
