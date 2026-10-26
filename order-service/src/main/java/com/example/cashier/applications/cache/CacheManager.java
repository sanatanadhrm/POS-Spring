package com.example.cashier.applications.cache;

import java.time.Duration;

public interface CacheManager {
    <T> T get(String key, Class<T> type);

    <T> void set(String key, T value, Duration ttl);

    Boolean setIfAbsent(String key, Object value, Duration ttl);

    void delete(String key);

    void setRaw(String key, String value, Duration ttl);

    String getRaw(String key);

}
