package com.pete.payment.app.data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ForecastSummaryDTO {
    private Set<String> allMerchantNames = new HashSet<>();

    private Map<Date, SummaryLineDTO> dailyData = new HashMap<>();

    public Set<String> getAllMerchantNames() {
        return Collections.unmodifiableSet(allMerchantNames);
    }

    public Map<Date, SummaryLineDTO> getDailyEntries() {
        return Collections.unmodifiableMap(dailyData);
    }

    public void setMerchantDailyEntry(String merchantName, Date forecastCollectedDay, BigDecimal amount) {
        allMerchantNames.add(merchantName);

        SummaryLineDTO dailyEntry = dailyData.get(forecastCollectedDay);

        if (dailyEntry == null) {
            dailyEntry = new SummaryLineDTO(forecastCollectedDay);
            dailyData.put(forecastCollectedDay, dailyEntry);
        }

        dailyEntry.setAmountForMerchant(merchantName, amount);
    }
}
