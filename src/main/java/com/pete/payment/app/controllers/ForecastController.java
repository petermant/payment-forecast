package com.pete.payment.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForecastController {

    @GetMapping("forecast/summary")
    public String forecastSummary() {
        return "forecast-summary";
    }
}
