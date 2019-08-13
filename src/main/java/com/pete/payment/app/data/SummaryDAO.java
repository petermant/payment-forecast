package com.pete.payment.app.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Component
public class SummaryDAO {

    private static String SUMMARY_QUERY = "select forecast.forecast_collected_day, m.MERCHANT_NAME, sum(forecast.AMOUNT) as daily_amount from " +
            "  ( " +
            "    select MERCHANT_ID, amount, formatdatetime(trunc(due_epoc) + case hour(DUE_EPOC)>=16 when true then 1 else 0 end, 'yyyy-MM-DD') as forecast_collected_day " +
            "    from payment " +
            "  ) forecast " +
            "inner join MERCHANT m on m.ID = forecast.MERCHANT_ID " +
            "group by forecast_collected_day, MERCHANT_ID";

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private DataSource dataSource;


    public SummaryDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ForecastSummaryDTO getSummaryResults() {

        try (Connection c = dataSource.getConnection()) {
            ForecastSummaryDTO results = new ForecastSummaryDTO();

            ResultSet rs = c.prepareStatement(SUMMARY_QUERY).executeQuery();

            // each row has 3 columns: forecast_collected_day, merchant_name, daily_amount
            while (rs.next()) {
                addEntry(results, rs);
            }

            return results;

        } catch (SQLException e) {
            logger.error("Unable to retrieve summary results due to SQL exception", e);

            // TODO wire up exception handling with server error response
            return null;
        }
    }

    private void addEntry(ForecastSummaryDTO results, ResultSet rs) throws SQLException {
        String merchantName = rs.getString("merchant_name");
        Date forecastCollectedDay = rs.getDate("forecast_collected_day");
        BigDecimal amount = rs.getBigDecimal("daily_amount");

        results.setMerchantDailyEntry(merchantName, forecastCollectedDay, amount);
    }
}
