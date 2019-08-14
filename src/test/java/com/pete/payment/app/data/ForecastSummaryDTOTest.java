package com.pete.payment.app.data;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ForecastSummaryDTOTest {

    private ForecastSummaryDTO forecastSummaryDTO;

    private LocalDateTime d1 = LocalDateTime.now(ZoneOffset.UTC);
    private LocalDateTime d2 = d1.plusDays(1);

    @Before
    public void beforeForecastSummaryDTOTest() {
        forecastSummaryDTO = new ForecastSummaryDTO();
        forecastSummaryDTO.setMerchantDailyEntry("merchant", d1, BigDecimal.ONE);
        forecastSummaryDTO.setMerchantDailyEntry("merchant", d2, BigDecimal.TEN);
        forecastSummaryDTO.setMerchantDailyEntry("merchant2", d1, BigDecimal.valueOf(11));
    }

    @Test
    public void getAllMerchantNamesReturnsList() {
        assertEquals(2, forecastSummaryDTO.getAllMerchantNames().size());
        assertTrue(forecastSummaryDTO.getAllMerchantNames().contains("merchant"));
        assertTrue(forecastSummaryDTO.getAllMerchantNames().contains("merchant2"));
    }

    @Test
    public void getDailyEntries() {
        final Map<LocalDateTime, SummaryLineDTO> dailyEntries = forecastSummaryDTO.getDailyEntries();

        assertEquals(2, dailyEntries.size());

        SummaryLineDTO firstEntry = dailyEntries.get(d1);
        SummaryLineDTO secondEntry = dailyEntries.get(d2);

        assertEquals(d1, firstEntry.getForecastDay());
        assertEquals(d2, secondEntry.getForecastDay());

        assertEquals(BigDecimal.ONE, firstEntry.getAmountForMerchant("merchant"));
        assertEquals(BigDecimal.TEN, secondEntry.getAmountForMerchant("merchant"));

        assertEquals(BigDecimal.valueOf(11), firstEntry.getAmountForMerchant("merchant2"));
        assertEquals(BigDecimal.ZERO, secondEntry.getAmountForMerchant("merchant2"));

    }
}