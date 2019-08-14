package com.pete.payment.app.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SummaryLineDTO {

    private LocalDateTime forecastDay;

    private Map<String, BigDecimal> amountPerMerchant = new HashMap<>();

    public SummaryLineDTO(LocalDateTime forecastDay) {
        this.forecastDay = forecastDay;
    }

    public LocalDateTime getForecastDay() {
        return forecastDay;
    }

    public BigDecimal getAmountForMerchant(String name) {
        BigDecimal amount = amountPerMerchant.get(name);

        return amount == null ? BigDecimal.ZERO : amount;
    }

    public void setAmountForMerchant(String name, BigDecimal decimal) {
        amountPerMerchant.put(name, decimal);
    }
}
