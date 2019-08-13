package com.pete.payment.app.controllers;

import com.pete.payment.app.data.ForecastSummaryDTO;
import com.pete.payment.app.services.ForecastService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForecastController {

    private ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping("forecast/summary")
    public String forecastSummary(Model model) {
        ForecastSummaryDTO summary = forecastService.getForecastSummary();
        model.addAttribute("merchants", summary.getAllMerchantNames());
        model.addAttribute("dailyEntries", summary.getDailyEntries().values());
        return "forecast-summary";
    }
}
