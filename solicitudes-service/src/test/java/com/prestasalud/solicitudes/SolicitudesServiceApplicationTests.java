package com.prestasalud.solicitudes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.TimeZone;

@SpringBootTest
class SolicitudesServiceApplicationTests {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
	}

	@Test
	void contextLoads() {
	}
}