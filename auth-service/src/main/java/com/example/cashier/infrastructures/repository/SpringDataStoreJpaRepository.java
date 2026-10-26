package com.example.cashier.infrastructures.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.cashier.domains.store.entity.Store;

public interface SpringDataStoreJpaRepository extends JpaRepository<Store, String>, JpaSpecificationExecutor<Store> {
}