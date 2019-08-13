package com.pete.payment.app;

import com.pete.payment.app.services.ForecastService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test.properties")
@SpringBootTest
public class ForecastServiceTests {

    @Autowired
    private ForecastService forecastService;

    @Test
    public void canGetForecastServiceInstance() {
        assertNotNull(forecastService);
    }
}
