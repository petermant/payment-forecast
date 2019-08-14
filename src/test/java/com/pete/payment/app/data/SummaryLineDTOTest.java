package com.pete.payment.app.data;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SummaryLineDTOTest {

    private SummaryLineDTO payeeSummaryDTO;

    @Before
    public void beforePayeeSummaryDTOTest() {
        payeeSummaryDTO = new SummaryLineDTO(LocalDateTime.now(ZoneOffset.UTC));
        payeeSummaryDTO.setAmountForMerchant("merchantname", BigDecimal.TEN);
    }

    @Test
    public void dayIsSet() {
        assertNotNull(payeeSummaryDTO.getForecastDay());
    }

    @Test
    public void getAmountForMerchantReturnsAmountWhenPresent() {
        assertEquals(BigDecimal.TEN, payeeSummaryDTO.getAmountForMerchant("merchantname"));
    }

    @Test
    public void getAmountForMerchantReturnsZeroWhenMerchantNotPresent() {
        assertEquals(BigDecimal.ZERO, payeeSummaryDTO.getAmountForMerchant("non existent merchantname"));
    }

    @Test
    public void setAmountForMerchant() {
        payeeSummaryDTO.setAmountForMerchant("other", BigDecimal.ONE);
        assertEquals(BigDecimal.ONE, payeeSummaryDTO.getAmountForMerchant("other"));
    }
}