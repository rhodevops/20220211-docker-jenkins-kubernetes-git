package com.paymentchain.billing;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BasicApplicationTests {

	@Test
	public void contextLoads() {
		String message = "default message cambio testi devops";
		// prueba que valida que el mensaje anterior no es nulo
		Assert.assertNotNull(message);
	}

}
