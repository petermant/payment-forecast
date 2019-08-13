package com.pete.payment.dataload.itemhandlers;

import com.pete.payment.dataload.dto.PaymentLine;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class RowValidator implements Validator<PaymentLine> {
    static final String BAD_SHA = "Calculated SHA value did not match SHA value in input file";
    static final String DUE_DATES_DIFFERENT = "Due date EPOC and Due date UTC did not match";
    static final String DUE_EPOC_ABSENT = "Due date EPOC was absent";
    static final String DUE_UTC_ABSENT = "Due date UTC was absent";

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public void validate(PaymentLine row) throws ValidationException {

        if (row.getDueEpoc() == null) {
            throw new ValidationException(DUE_EPOC_ABSENT);
        } else if (row.getDueUTC() == null) {
            throw new ValidationException(DUE_UTC_ABSENT);
        } else if (!row.getDueEpoc().equals(row.getDueUTC().getTime()/1000)) {
            throw new ValidationException(DUE_DATES_DIFFERENT);
        } else if (!calculatedSHA(row).equals(row.getSHA256())) {
            throw new ValidationException(BAD_SHA);
        }
    }

    private String calculatedSHA(PaymentLine row) {
        String dueEpoc = Long.toString(row.getDueEpoc());
        String stringToHash = row.getMerchantPubKey() + row.getPayerPubKey() + row.getDebitPermissionId() + dueEpoc + row.getAmount();
        return DigestUtils.sha256Hex(stringToHash);
    }
}
