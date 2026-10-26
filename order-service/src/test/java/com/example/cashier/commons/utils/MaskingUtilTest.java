package com.example.cashier.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MaskingUtilTest {

    @Test
    @DisplayName("Harus menyamarkan token JWT dengan mengambil 6 karakter awal dan 4 karakter akhir")
    void shouldMaskToken_WhenTokenIsValid() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozGz";
        String masked = MaskingUtil.maskToken(token);

        assertEquals("eyJhbG****dozGz".substring(0, 10), masked.substring(0, 10));
        assertEquals("ozGz", masked.substring(masked.length() - 4));
    }

    @Test
    @DisplayName("Harus mengembalikan bintang jika token null atau terlalu pendek")
    void shouldReturnStars_WhenTokenIsNullOrTooShort() {
        assertEquals("******", MaskingUtil.maskToken(null));
        assertEquals("******", MaskingUtil.maskToken(""));
        assertEquals("******", MaskingUtil.maskToken("12345"));
    }

    @Test
    @DisplayName("Harus menyamarkan nomor rekening dengan format 1234****90")
    void shouldMaskAccountNumber_WhenValid() {
        String accNumber = "5270123456";
        String masked = MaskingUtil.maskAccountNumber(accNumber);

        assertEquals("5270****56", masked);
    }

    @Test
    @DisplayName("Harus menyamarkan nomor handphone")
    void shouldMaskPhone_WhenValid() {
        String phone = "081234567890";
        String masked = MaskingUtil.maskPhone(phone);

        assertEquals("0812****890", masked);
    }

    @Test
    @DisplayName("Harus menyamarkan email")
    void shouldMaskEmail_WhenValid() {
        String email = "budi.santoso@bank.co.id";
        String masked = MaskingUtil.maskEmail(email);

        assertEquals("b***o@bank.co.id", masked);
    }
}
