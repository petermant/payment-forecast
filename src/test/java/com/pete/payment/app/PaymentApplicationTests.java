package com.pete.payment.app;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test.properties")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = WebApplicationTestConfig.class)
public class PaymentApplicationTests {

	@LocalServerPort
	private int port;

	private URL base;

	@Autowired
	private TestRestTemplate template;

	@Before
	public void beforePaymentApplicationTests() throws Exception {
		this.base = new URL("http://localhost:" + port + "/forecast/summary");
	}

	@Test
	public void forecastSummaryShouldReturnOK() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		assertEquals(200, response.getStatusCode().value());
	}

	@Test
	public void forecastSummaryShouldContainTemplateText() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		assertNotNull(response.getBody());
		assertTrue(response.getBody().contains("<title>Forecast summary</title>"));
	}

	@Test
	public void forecastSummaryShouldContainHeaderCellWithBritishGasInWithoutUnderscores() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		final String body = response.getBody();
		assertNotNull(body);
		assertTrue(body.contains("<th>British Gas</th>"));
	}

	@Test
	public void forecastSummaryShouldContainRightAmountForBritishGas() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		final String body = response.getBody();
		assertNotNull(body);
		assertTrue(body.contains("<td>£1,064.50</td>"));
	}

}
