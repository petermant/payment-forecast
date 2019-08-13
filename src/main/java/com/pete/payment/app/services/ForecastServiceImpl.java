package com.pete.payment.app.services;

import com.pete.payment.app.data.ForecastSummaryDTO;
import com.pete.payment.app.data.SummaryDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ForecastServiceImpl implements ForecastService {

    private SummaryDAO summaryDAO;

    public ForecastServiceImpl(SummaryDAO summaryDAO) {
        this.summaryDAO = summaryDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public ForecastSummaryDTO getForecastSummary() {
        return summaryDAO.getSummaryResults();
    }
}
