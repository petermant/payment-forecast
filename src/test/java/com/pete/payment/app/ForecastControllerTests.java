package com.pete.payment.app;

import com.pete.payment.app.controllers.ForecastController;
import com.pete.payment.app.data.ForecastSummaryDTO;
import com.pete.payment.app.services.ForecastService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test.properties")
@SpringBootTest
public class ForecastControllerTests {

    @MockBean
    ForecastService forecastService;

    @Autowired
    ForecastController forecastController;

    @Test
    public void canGetControllerInstance() {
        assertNotNull(forecastController);
    }

    @Test
    public void controllerPutsDataIntoModel() {

        when(forecastService.getForecastSummary()).thenReturn(new ForecastSummaryDTO());

        Model model = mock(Model.class);
        forecastController.forecastSummary(model);

        verify(model).addAttribute(eq("merchants"), anySet());
        verify(model).addAttribute(eq("dailyEntries"), anyCollection());
    }

}
