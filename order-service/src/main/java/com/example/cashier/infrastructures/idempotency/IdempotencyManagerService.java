package com.example.cashier.infrastructures.idempotency;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.example.cashier.applications.indempotency.IndempotencyManager;
import com.example.cashier.infrastructures.cache.redis.RedisManager;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IdempotencyManagerService implements IndempotencyManager {

    private final RedisManager redisManager;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final String PENDING_STATUS = "PENDING";

    public IdempotencyManagerService(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    /**
     * Mengunci key menggunakan Redis SETNX dengan batas waktu (misal 60 detik).
     * 
     * @return true jika berhasil mengunci (request pertama), false jika key sudah
     *         ada.
     */
    public boolean acquireLock(String key, Duration lockTimeout) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        Boolean success = redisManager.setIfAbsent(redisKey, PENDING_STATUS, lockTimeout);
        return Boolean.TRUE.equals(success);
    }

    /**
     * Mengecek apakah request dengan key ini sedang diproses (PENDING).
     */
    public boolean isPending(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        String val = redisManager.getRaw(redisKey);
        return PENDING_STATUS.equals(val);
    }

    /**
     * Mengambil response yang sudah tersimpan di cache Redis jika transaksi
     * sebelumnya sudah sukses.
     */
    public <T> T getCachedResponse(String key, Class<T> targetClass) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        String val = redisManager.getRaw(redisKey);
        // Jika tidak ada atau masih PENDING, berarti belum ada response sukses
        if (val == null || PENDING_STATUS.equals(val)) {
            return null;
        }
        // Serahkan deserialisasi sepenuhnya ke RedisManager
        return redisManager.get(redisKey, targetClass);
    }

    /**
     * Menyimpan response transaksi yang sukses ke Redis dengan masa simpan TTL
     * (misal 24 jam).
     */
    public void saveResponse(String key, Object response, Duration ttl) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        redisManager.set(redisKey, response, ttl);
    }

    /**
     * Menghapus kunci dari Redis jika terjadi kegagalan/error di tengah proses
     * transaksi.
     */
    public void releaseLock(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        String currentVal = redisManager.getRaw(redisKey);
        if (PENDING_STATUS.equals(currentVal)) {
            redisManager.delete(redisKey);
        }
    }
}
