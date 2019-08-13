package com.pete.payment.app.data;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.*;

public class SummaryLineDTOTest {

    private SummaryLineDTO payeeSummaryDTO;

    @Before
    public void beforePayeeSummaryDTOTest() {
        payeeSummaryDTO = new SummaryLineDTO(new Date());
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