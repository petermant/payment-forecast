package com.pete.payment.app.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ForecastSummaryDTO {
    private Set<String> allMerchantNames = new HashSet<>();

    private Map<LocalDateTime, SummaryLineDTO> dailyData = new HashMap<>();

    public Set<String> getAllMerchantNames() {
        return Collections.unmodifiableSet(allMerchantNames);
    }

    public Map<LocalDateTime, SummaryLineDTO> getDailyEntries() {
        return Collections.unmodifiableSortedMap(new TreeMap<>(dailyData));
    }

    public void setMerchantDailyEntry(String merchantName, LocalDateTime forecastCollectedDay, BigDecimal amount) {
        allMerchantNames.add(merchantName);

        SummaryLineDTO dailyEntry = dailyData.get(forecastCollectedDay);

        if (dailyEntry == null) {
            dailyEntry = new SummaryLineDTO(forecastCollectedDay);
            dailyData.put(forecastCollectedDay, dailyEntry);
        }

        dailyEntry.setAmountForMerchant(merchantName, amount);
    }
}
