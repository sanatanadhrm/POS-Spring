package com.example.cashier.applications.indempotency;

import java.time.Duration;

public interface IndempotencyManager {

    boolean acquireLock(String key, Duration lockTimeout);

    boolean isPending(String key);

    <T> T getCachedResponse(String key, Class<T> targetClass);

    void saveResponse(String key, Object response, Duration ttl);

    void releaseLock(String key);
}
