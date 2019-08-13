package com.pete.payment.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

@Configuration
@ComponentScan("com.pete.payment.app")
public class WebApplicationTestConfig {

    private EmbeddedDatabase database;

    @Bean
    public DataSource dataSource() {
        database = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScripts("sql-scripts/create-tables.sql", "sql-scripts/load-data.sql").build();
        return database;
    }

    @PreDestroy
    public void preDestroy() {
        database.shutdown();
    }
}
