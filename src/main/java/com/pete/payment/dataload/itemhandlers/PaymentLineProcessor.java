package com.pete.payment.dataload.itemhandlers;

import com.pete.payment.dataload.domain.Merchant;
import com.pete.payment.dataload.domain.Payer;
import com.pete.payment.dataload.domain.Payment;
import com.pete.payment.dataload.dto.PaymentLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class PaymentLineProcessor implements ItemProcessor<PaymentLine, Payment> {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private RowValidator validator;

    @Override
    public Payment process(PaymentLine row) {

        // this throws RuntimeExceptions which are logged in the PaymentItemListener
        validator.validate(row);

        Merchant merchant = new Merchant();
        merchant.setId(row.getMerchantID());
        merchant.setMerchantName(row.getMerchantName());
        merchant.setPublicKey(row.getMerchantPubKey());

        Payer payer = new Payer();
        payer.setId(row.getPayerID());
        payer.setPayerPublicKey(row.getPayerPubKey());

        Payment payment = new Payment();

        payment.setPayer(payer);
        payment.setMerchant(merchant);

        payment.setReceivedUTC(row.getReceivedUTC());
        payment.setDebitPermissionId(row.getDebitPermissionId());
        payment.setDueEpoc(LocalDateTime.ofEpochSecond(row.getDueEpoc(), 0, ZoneOffset.UTC));
        payment.setCurrency(row.getCurrency());
        payment.setAmount(row.getAmount());

        return payment;
    }
}
