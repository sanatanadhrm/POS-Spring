package com.example.cashier.commons.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.cashier.domains.user.entity.User;
import com.example.cashier.infrastructures.repository.SpringDataRoleJpaRepository;
import com.example.cashier.infrastructures.repository.SpringDataStoreJpaRepository;
import com.example.cashier.infrastructures.repository.SpringDataUserJpaRepository;
import com.example.cashier.domains.role.entity.Role;
import com.example.cashier.domains.store.entity.Store;

import lombok.extern.slf4j.Slf4j;

@Slf4j // Untuk mencetak info di terminal
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final SpringDataUserJpaRepository userRepository;
    private final SpringDataRoleJpaRepository roleRepository;
    private final SpringDataStoreJpaRepository storeJpaRepository;
    private final PasswordEncoder passwordEncoder; // Untuk nge-hash password

    public DatabaseSeeder(SpringDataUserJpaRepository userRepository, SpringDataRoleJpaRepository roleRepository,
            SpringDataStoreJpaRepository storeJpaRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.storeJpaRepository = storeJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Cek apakah Superadmin sudah ada di database?
        User existingAdmin = userRepository.findByUsername("superadmin").orElse(null);
        User existingCashier = userRepository.findByUsername("cashier-1").orElse(null);
        Store existingStore = storeJpaRepository.findById("60e2b953-80a2-4191-b1e0-cf91de14ad79").orElse(null);

        if (existingAdmin == null) {
            existingUser("superadmin", "SUPER_ADMIN", existingStore);
        } else {
            log.info("Superadmin sudah ada, Seeder dilewati.");
        }

        if (existingCashier == null) {
            existingUser("cashier-1", "CASHIER", existingStore);
        } else {
            log.info("Cashier sudah ada, Seeder dilewati.");

        }

    }

    private void existingUser(String usernme, String rolename, Store store) {
        log.info("Membuat Akun user Baru...");
        // 2. Buat Objek Role (Anggap saja id 'SUPERADMIN' sudah dibuat oleh Flyway)

        Role userRole = roleRepository.findByName(rolename).orElse(null);

        if (userRole != null) {
            User admin = User.builder()
                    .username(usernme)
                    .password(passwordEncoder.encode("password"))
                    .role(userRole)
                    .store(rolename.equals("SUPER_ADMIN") ? null : store)
                    .build();

            userRepository.save(admin);
            log.info("Sukses Membuat User {}", usernme);
        } else {
            log.error("Role {} tidak ditemukan!", rolename);
        }
    }
}
