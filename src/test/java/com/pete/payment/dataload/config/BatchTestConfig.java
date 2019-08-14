package com.pete.payment.dataload.config;

import com.pete.payment.dataload.itemhandlers.RowValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@Configuration
public class BatchTestConfig {

    @Bean
    @Primary
    protected RowValidator rowValidator() {
        return mock(RowValidator.class);
    }
}
