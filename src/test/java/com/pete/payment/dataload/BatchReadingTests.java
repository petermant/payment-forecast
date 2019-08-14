package com.pete.payment.dataload;

import com.pete.payment.dataload.config.BatchConfig;
import com.pete.payment.dataload.config.BatchTestConfig;
import com.pete.payment.dataload.domain.Payment;
import com.pete.payment.dataload.dto.PaymentLine;
import com.pete.payment.dataload.itemhandlers.RowValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ComponentScan("com.pete.payment.dataload")
@ContextConfiguration(classes = {BatchConfig.class, BatchTestConfig.class})
@TestPropertySource("classpath:test.properties")
@TestExecutionListeners(listeners = DirtiesContextTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BatchReadingTests {

    @Autowired
    @Lazy
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private RowValidator rowValidator;

    @PersistenceContext
    private EntityManager entityManager;

    private String header = "ReceivedUTC,MerchantId,MerchantName,MerchantPubKey,PayerId,PayerPubKey,DebitPermissionId,DueUTC,DueEpoc,Currency,Amount,SHA256";

    private String validLine = "2020-01-03T10:48:11Z,1,Sky,KKkhBNSD6pIad48vG4Er,114985,lsOGMOVFH8kEGDnt1uM9,7922750,2020-01-06T00:00:01Z,1578268801,GBP,64.40,7d1116866e9dbed9a0df5aef46c24743e0f9e79a7563f33674430f4547cf6a14";

    private String badDateLine = "2020-01-34T10:48:11Z,1,Sky,KKkhBNSD6pIad48vG4Er,114985,lsOGMOVFH8kEGDnt1uM9,7922750,2020-01-06T00:00:01Z,1578268801,GBP,64.40,7d1116866e9dbed9a0df5aef46c24743e0f9e79a7563f33674430f4547cf6a14";

    private String invalidLine = "hello,this,is,not,structured,correctly";

    @Autowired
    private FlatFileItemReader<PaymentLine> reader;

    @Test
    public void processorShouldCheckAndPersistAValidLine() {

        reader.setResource(new ByteArrayResource((header + System.lineSeparator() + validLine).getBytes()));
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("read");

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        verify(rowValidator, times(1)).validate(any(PaymentLine.class));

        TypedQuery<Payment> query = entityManager.createQuery("from Payment", Payment.class);
        List<Payment> results = query.getResultList();
        assertEquals(1, results.size());

        Payment resultEntry = results.get(0);

        assertEquals("2020-01-03T10:48:11", resultEntry.getReceivedUTC().toString());

        assertEquals(1, resultEntry.getMerchant().getId().intValue());
        assertEquals("Sky", resultEntry.getMerchant().getMerchantName());
        assertEquals("KKkhBNSD6pIad48vG4Er", resultEntry.getMerchant().getPublicKey());

        assertEquals(114985, resultEntry.getPayer().getId().intValue());
        assertEquals("lsOGMOVFH8kEGDnt1uM9", resultEntry.getPayer().getPayerPublicKey());

        assertEquals(7922750, resultEntry.getDebitPermissionId().intValue());

        // can't test this as didn't store the attribute
        //assertEquals("2020-01-06T00:00:01", resultEntry.getDueUTC());

        // This is the same as the input but with the additional three zeroes as Java dates store epoc in millis not seconds as per the input file
        assertEquals(1578268801, resultEntry.getDueEpoc().toEpochSecond(ZoneOffset.UTC));
        assertEquals("GBP", resultEntry.getCurrency().toString());
        assertEquals(new BigDecimal("64.40"), resultEntry.getAmount());
    }

    @Test
    public void processorShouldNotPersistAnInvalidLine() {
        doThrow(new ValidationException("invalid line")).when(rowValidator).validate(any()); // e.g. if SHA code not valid, or UTC, or something - i.e. validator throws an Exception. This isn't same as when CSV line is badly structured.

        reader.setResource(new ByteArrayResource((header + System.lineSeparator() + validLine).getBytes()));

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("read");

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        verify(rowValidator, times(1)).validate(any(PaymentLine.class));

        TypedQuery<Payment> query = entityManager.createQuery("from Payment", Payment.class);
        List<Payment> results = query.getResultList();
        assertEquals(0, results.size());
    }

    @Test
    public void processorShouldNotValidateOrPersistLinesWithInvalidDate() {

        reader.setResource(new ByteArrayResource((header + System.lineSeparator() + badDateLine).getBytes()));

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("read");

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        verify(rowValidator, times(0)).validate(any(PaymentLine.class));

        TypedQuery<Payment> query = entityManager.createQuery("from Payment", Payment.class);
        List<Payment> results = query.getResultList();
        assertEquals(0, results.size());
    }

    @Test
    public void processorShouldNotValidateOrPersistLinesWithInsufficientColumns() {

        reader.setResource(new ByteArrayResource((header + System.lineSeparator() + invalidLine).getBytes()));

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("read");

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        verify(rowValidator, times(0)).validate(any(PaymentLine.class));

        TypedQuery<Payment> query = entityManager.createQuery("from Payment", Payment.class);
        List<Payment> results = query.getResultList();
        assertEquals(0, results.size());
    }


    @Test
    public void processorShouldNotValidateOrPersistLinesWithTooManyColumns() {

        reader.setResource(new ByteArrayResource((header + System.lineSeparator() + validLine+","+invalidLine).getBytes()));

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("read");

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        verify(rowValidator, times(0)).validate(any(PaymentLine.class));

        TypedQuery<Payment> query = entityManager.createQuery("from Payment", Payment.class);
        List<Payment> results = query.getResultList();
        assertEquals(0, results.size());
    }

    @Test
    public void processorShouldNotPersistLinesWithInvalidSHA() {

        doCallRealMethod().when(rowValidator).validate(any());

        // add an X onto the end of the SHA should break it
        reader.setResource(new ByteArrayResource((header + System.lineSeparator() + validLine + "X").getBytes()));

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("read");

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        verify(rowValidator, times(1)).validate(any(PaymentLine.class));

        TypedQuery<Payment> query = entityManager.createQuery("from Payment", Payment.class);
        List<Payment> results = query.getResultList();
        assertEquals(0, results.size());
    }

}
