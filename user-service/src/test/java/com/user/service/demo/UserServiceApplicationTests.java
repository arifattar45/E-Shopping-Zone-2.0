package com.user.service.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
	    "eureka.client.enabled=false",
	    "spring.cloud.discovery.enabled=false"
	})
@AutoConfigureMockMvc(addFilters = false) 
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
