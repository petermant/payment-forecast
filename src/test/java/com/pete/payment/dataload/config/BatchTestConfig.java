package com.pete.payment.dataload.config;

import com.pete.payment.dataload.itemhandlers.RowValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

@Configuration
public class BatchTestConfig {

    @Bean
    protected DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;");
        return ds;
    }

    @Bean
    @Primary
    protected RowValidator rowValidator() {
        return mock(RowValidator.class);
    }

//    @Bean
//    protected JobLauncherTestUtils jobLauncherTestUtils() {
//        return new JobLauncherTestUtils();
//    }
}
