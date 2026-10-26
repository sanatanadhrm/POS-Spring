package com.example.cashier.infrastructures.database.postgresql;

import org.springframework.stereotype.Component;

import com.example.cashier.domains.store.StoreRepository;
import com.example.cashier.domains.store.entity.Store;
import com.example.cashier.infrastructures.repository.SpringDataStoreJpaRepository;

@Component
public class StoreRepositoryPostgreImpl implements StoreRepository {

    private final SpringDataStoreJpaRepository jpaRepository;

    public StoreRepositoryPostgreImpl(SpringDataStoreJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Store store) {
        this.jpaRepository.save(store);
    }

    @Override
    public Store getByStoreId(String storeId) {
        return this.jpaRepository.findById(storeId).orElse(null);
    }

}
