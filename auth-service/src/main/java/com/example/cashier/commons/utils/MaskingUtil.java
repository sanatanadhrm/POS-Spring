package com.example.cashier.commons.utils;

/**
 * Utility helper untuk menyamarkan data sensitif (PII - Personally Identifiable Information)
 * agar tidak bocor di file log maupun third-party monitoring (Standar Perbankan / PCI-DSS).
 */
public final class MaskingUtil {

    private MaskingUtil() {
        // Private constructor agar class utility ini tidak bisa di-instansiasi
    }

    /**
     * Menyamarkan Token JWT / Secret: eyJhbGciOi... -> eyJhbG****W9w
     */
    public static String maskToken(String token) {
        if (token == null || token.isBlank()) {
            return "******";
        }
        if (token.length() <= 10) {
            return "******";
        }
        return token.substring(0, 6) + "****" + token.substring(token.length() - 4);
    }

    /**
     * Menyamarkan Nomor Rekening / Kartu ATM: 1234567890 -> 1234****90
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return "******";
        }
        if (accountNumber.length() <= 6) {
            return "******";
        }
        return accountNumber.substring(0, 4) + "****" + accountNumber.substring(accountNumber.length() - 2);
    }

    /**
     * Menyamarkan Nomor Telepon / HP: 081234567890 -> 0812****7890
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank() || phone.length() <= 6) {
            return "******";
        }
        return phone.substring(0, 4) + "****" + phone.substring(phone.length() - 3);
    }

    /**
     * Menyamarkan Email: budi.santoso@bank.co.id -> b***o@bank.co.id
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "******";
        }
        String[] parts = email.split("@", 2);
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "*@" + domain;
        }

        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + "@" + domain;
    }
}
