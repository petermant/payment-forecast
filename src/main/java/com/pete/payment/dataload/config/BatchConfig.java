package com.pete.payment.dataload.config;

import com.pete.payment.dataload.domain.Payment;
import com.pete.payment.dataload.dto.PaymentLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private DataSource dataSource;

    @Value("${input-filename:payment-forecast-data.csv}")
    private String fileName;

    // e.g. 2020-01-06T00:00:01Z
    private static SimpleDateFormat DATE_FORMAT;

    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DATE_FORMAT.setLenient(false);
    }

    @Bean
    protected ResourcelessTransactionManager resourcelessTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public Job job(Step read, Step summarise) {
        return jobs.get("payment-import").start(read).start(summarise).build();
    }

    @Bean
    protected Step read(ItemReader<PaymentLine> reader,
                        @Qualifier("paymentLineProcessor") ItemProcessor<PaymentLine, Payment> processor,
                        ItemWriter<Payment> writer,
                        ItemListenerSupport<PaymentLine, Payment> itemListener) {
        return steps.get("read")
                .<PaymentLine, Payment> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener((ItemReadListener<PaymentLine>)itemListener)
                .listener((ItemProcessListener<PaymentLine, Payment>)itemListener)
                .faultTolerant().skipPolicy(new AlwaysSkipItemSkipPolicy())
                .build();
    }

    @Bean
    protected Resource csvResource() {
        return new ClassPathResource(fileName);
    }

    @Bean
    protected FlatFileItemReader<PaymentLine> reader(LineTokenizer lineTokenizer, FieldSetMapper<PaymentLine> fieldSetMapper, Resource csvResource) {
        FlatFileItemReader<PaymentLine> reader = new FlatFileItemReaderBuilder<PaymentLine>()
                .lineTokenizer(lineTokenizer)
                .fieldSetMapper(fieldSetMapper)
                .name("PaymentLineReader")
                .build();

        reader.setResource(csvResource);
        reader.setLinesToSkip(1);

        return reader;
    }

    @Bean
    protected LineTokenizer lineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("ReceivedUTC","MerchantId","MerchantName","MerchantPubKey","PayerId","PayerPubKey","DebitPermissionId","DueUTC","DueEpoc","Currency","Amount","SHA256");
        return lineTokenizer;
    }

    @Bean
    protected FieldSetMapper<PaymentLine> fieldSetMapper() {
        BeanWrapperFieldSetMapper<PaymentLine> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(PaymentLine.class);
        mapper.setCustomEditors(Collections.singletonMap(Date.class, new CustomDateEditor(DATE_FORMAT, false)));
        return mapper;
    }

    private long startRowCount = 0;

    @PostConstruct
    public void beforeStart() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            ResultSet rs = c.prepareStatement("select count(*) from Payment").executeQuery();
            if (rs.next()) {
                startRowCount = rs.getLong(1);
            }
        }
    }

    @PreDestroy
    public void onCompletion() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            ResultSet rs = c.prepareStatement("select count(*) from Payment").executeQuery();
            if (rs.next()) {
                long rowCount = rs.getLong(1);
                logger.info("Wrote {} rows to the database ", rowCount - startRowCount);
            }
        }
    }
}
