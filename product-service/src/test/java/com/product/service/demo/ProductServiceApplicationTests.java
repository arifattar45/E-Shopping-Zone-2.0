package com.product.service.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
	    "eureka.client.enabled=false",
	    "spring.cloud.discovery.enabled=false"
	})
class ProductServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
