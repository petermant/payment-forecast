package com.pete.payment.app.services;

import org.springframework.stereotype.Component;

@Component
public class ForecastServiceImpl implements ForecastService {

    @Override
    public String getForecastSummary() {
        return "forecast summary results here";
    }
}
