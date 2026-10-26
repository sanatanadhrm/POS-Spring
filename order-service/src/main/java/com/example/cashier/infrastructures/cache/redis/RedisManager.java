package com.example.cashier.infrastructures.cache.redis;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.example.cashier.applications.cache.CacheManager;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class RedisManager implements CacheManager {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisManager(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setRaw(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public String getRaw(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public <T> T get(String key, Class<T> targetClass) {
        String value = (String) redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.readValue(value, targetClass);
        } catch (Exception e) {
            log.error("Failed to deserialize cache for key: {}", key, e);
            return null;
        }
    }

    @Override
    public <T> void set(String key, T value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
        } catch (Exception e) {
            log.error("Failed to serialize cache for key: {}", key, e);
        }
    }

    @Override
    public Boolean setIfAbsent(String key, Object value, Duration ttl) {
        try {
            // Jika sudah String (seperti "PENDING"), jangan di-quote dua kali oleh Jackson
            String stringValue = (value instanceof String str)
                    ? str
                    : objectMapper.writeValueAsString(value);
            return redisTemplate.opsForValue().setIfAbsent(key, stringValue, ttl);
        } catch (Exception e) {
            log.error("Failed to setIfAbsent cache for key: {}", key, e);
            return false;
        }
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
