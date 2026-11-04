package com.example.cashier;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Membutuhkan database eksternal yang aktif (PostgreSQL/Redis)")

class CashierApplicationTests {

	@Test
	void contextLoads() {
	}

}
