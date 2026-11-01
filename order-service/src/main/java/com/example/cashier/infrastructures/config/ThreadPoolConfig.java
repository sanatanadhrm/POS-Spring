package com.example.cashier.infrastructures.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    /**
     * Dedicated Thread Pool untuk proses notifikasi (WhatsApp, Email, Struk, dll).
     * Mencegah pemakaian ForkJoinPool bawaan JVM agar tidak membebani komputasi sistem lain.
     */
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);                       // Minimal 5 thread standby
        executor.setMaxPoolSize(20);                      // Maksimal 20 thread saat antrean membludak
        executor.setQueueCapacity(100);                   // Antrean memori sebelum thread dinaikkan ke maxPoolSize
        executor.setThreadNamePrefix("notif-pool-");      // Prefix nama thread untuk memudahkan debugging/tracing
        executor.setWaitForTasksToCompleteOnShutdown(true); // Tunggu task selesai saat aplikasi dimatikan
        executor.setAwaitTerminationSeconds(30);          // Toleransi waktu tunggu graceful shutdown (30 detik)
        executor.initialize();
        return executor;
    }
}
