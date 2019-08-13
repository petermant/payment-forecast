package com.pete.payment.dataload.itemhandlers;

import com.pete.payment.dataload.domain.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Component
public class PaymentWriter extends JpaItemWriter<Payment> implements ItemWriter<Payment> {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    public void PaymentWriterPostConstruct() {
        super.setEntityManagerFactory(entityManagerFactory);
    }

    @Override
    public void write(List<? extends Payment> list) {
        if (logger.isDebugEnabled()) {
            logger.debug("Received {} elements for writing", list.size());
        }
        super.write(list);
    }
}
