package com.pete.payment.dataload.itemhandlers;

import com.pete.payment.dataload.domain.Payment;
import com.pete.payment.dataload.dto.PaymentLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentItemListener extends ItemListenerSupport<PaymentLine, Payment> {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Logger errorLinesLog = LoggerFactory.getLogger("batch-error-lines");

    public void beforeRead() {
        if (logger.isDebugEnabled())
            logger.debug("Starting to read next item");
    }

    public void afterRead(PaymentLine item) {
        if (logger.isDebugEnabled())
            logger.debug("Read item: {}", item);
    }

    @Override
    public void onReadError(Exception ex) {

        if (ex instanceof FlatFileParseException) {
            FlatFileParseException ffpe = (FlatFileParseException)ex;
            logger.error("Error parsing line {}: {}", ffpe.getLineNumber(), ffpe.getMessage());
            errorLinesLog.error("Error parsing line {}: {}", ffpe.getLineNumber(), ffpe.getMessage());
            errorLinesLog.error("Error was {}", ffpe.getCause().getMessage());
            errorLinesLog.error(ffpe.getInput());
            errorLinesLog.error("");
        } else {
            logger.error("Error reading line", ex);
        }
    }

    @Override
    public void onProcessError(PaymentLine item, Exception e) {
        if (e instanceof ValidationException) {
            logger.error("Line {} failed validation: {}", item.getItemCount(), e.getMessage());
        } else {
            logger.error("Error processing: ", e);
        }

        errorLinesLog.error("Error parsing line {}: {}", item.getItemCount(), e.getMessage());
        if (e.getCause() != null) {
            errorLinesLog.error("Error was {}", e.getCause().getMessage());
        }
        errorLinesLog.error(item.toString());
        errorLinesLog.error("");
    }

    @Override
    public void onWriteError(Exception ex, List<? extends Payment> item) {
        logger.error("Error writing list", ex);
    }
}
