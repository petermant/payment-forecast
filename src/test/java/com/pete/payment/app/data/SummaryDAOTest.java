package com.pete.payment.app.data;

import com.pete.payment.app.WebApplicationTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WebApplicationTestConfig.class)
public class SummaryDAOTest {

    @Autowired
    private SummaryDAO summaryDao;

    @Test
    public void getSummaryResultsReturnsAResult() {
        ForecastSummaryDTO dto = summaryDao.getSummaryResults();
        assertNotNull(dto.getDailyEntries());
    }

    @Test
    public void getSummaryResultsPutsPayerThreeAmountIntoTheNextDay() {
        ForecastSummaryDTO dto = summaryDao.getSummaryResults();

        // 1578441600000 is 2020-01-08
        SummaryLineDTO entryFor8th = dto.getDailyEntries().get(new Date(1578441600000L));
        assertEquals(new BigDecimal("91000.00"), entryFor8th.getAmountForMerchant("British_Gas"));
    }

    @Test
    public void totalIsCorrectForBritishGasWhichHasTwoEntriesOnSameDay() {

        BigDecimal expectedValue = new BigDecimal("1064.50");

        ForecastSummaryDTO dto = summaryDao.getSummaryResults();

        // 1578355200000L is 2020-01-07
        final SummaryLineDTO entryFor7th = dto.getDailyEntries().get(new Date(1578355200000L));

        assertEquals(expectedValue, entryFor7th.getAmountForMerchant("British_Gas"));
    }

    @Test
    public void thereAreNoResultsForVodafoneInTestDataSoItsNotInSummaryAndThereforeIsZeroInAmountForMerchant() {
        ForecastSummaryDTO dto = summaryDao.getSummaryResults();

        assertFalse(dto.getAllMerchantNames().contains("Vodafone"));
        assertFalse(dto.getDailyEntries().values().stream().anyMatch(v -> !v.getAmountForMerchant("Vodafone").equals(BigDecimal.ZERO)));
    }
}