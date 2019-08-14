package com.pete.payment.dataload.itemhandlers;

import com.pete.payment.dataload.dto.PaymentLine;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RowValidatorTest {

    private RowValidator validator = new RowValidator();

    @Test
    public void shouldValidateDueDatesWithoutNPE() {
        PaymentLine rowWithBadDueDates = new PaymentLine();
        String message1 = "", message2 = "", message3 = "";
        try {
            validator.validate(rowWithBadDueDates);
        } catch (ValidationException e) {
            message1 = e.getMessage();
        }
        assertEquals(RowValidator.DUE_EPOC_ABSENT, message1);

        rowWithBadDueDates.setDueEpoc(LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC));
        try {
            validator.validate(rowWithBadDueDates);
        } catch (ValidationException e) {
            message2 = e.getMessage();
        }
        assertEquals(RowValidator.DUE_UTC_ABSENT, message2);
        rowWithBadDueDates.setDueUTC(LocalDateTime.ofEpochSecond(rowWithBadDueDates.getDueEpoc(), 0, ZoneOffset.UTC).plusDays(1));
        try {
            validator.validate(rowWithBadDueDates);
        } catch (ValidationException e) {
            message3 = e.getMessage();
        }
        assertEquals(RowValidator.DUE_DATES_DIFFERENT, message3);
    }

    @Test
    public void shouldValidateThatDueUTCMatchesDueEPOC() {
        PaymentLine rowWithBadDueDates = new PaymentLine();
        rowWithBadDueDates.setDueEpoc(LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC));
        rowWithBadDueDates.setDueUTC(LocalDateTime.now(ZoneOffset.UTC).plusDays(2));
        String message = "";
        try {
            validator.validate(rowWithBadDueDates);
        } catch (ValidationException e) {
            message = e.getMessage();
        }
        assertEquals(RowValidator.DUE_DATES_DIFFERENT, message);
    }

    @Test
    public void shouldValidateThatSHA256IsCorrect() {
        PaymentLine row = new PaymentLine();
        row.setDueEpoc(1578268801L); // 2020-01-06T00:00:01Z
        row.setDueUTC(LocalDateTime.ofEpochSecond(row.getDueEpoc(), 0, ZoneOffset.UTC));
        row.setMerchantPubKey("");
        row.setAmount(BigDecimal.ONE);
        row.setDebitPermissionId(BigInteger.ONE);
        row.setPayerPubKey("hello");
        String shaOfDateAndDebitIdOfOneAndHelloThenOne = "6ebd6bfa7484870cd15383c07ade37212a89fdef97a501c21fcb16a89b5c4a3a";
        row.setSHA256(shaOfDateAndDebitIdOfOneAndHelloThenOne);
        String message = null;
        try {
            validator.validate(row);
        } catch (ValidationException e) {
            message = e.getMessage();
        }
        assertNull(message);
    }

    @Test
    public void shouldReturnMessageWhenSHAIsIncorrect() {
        PaymentLine rowWithBadSHA = new PaymentLine();
        rowWithBadSHA.setDueEpoc(LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC));
        rowWithBadSHA.setDueUTC(LocalDateTime.ofEpochSecond(rowWithBadSHA.getDueEpoc(), 0, ZoneOffset.UTC));
        rowWithBadSHA.setPayerPubKey("hello");
        rowWithBadSHA.setSHA256("not a sha");
        String message = "";
        try {
            validator.validate(rowWithBadSHA);
        } catch (ValidationException e) {
            message = e.getMessage();
        }
        assertEquals(RowValidator.BAD_SHA, message);
    }

    @Test
    public void checkSHACalculationIsCorrect() {
        // this uses a valid line with known attributes and a known SHA, from the test file
        String expectedSHA = "7d1116866e9dbed9a0df5aef46c24743e0f9e79a7563f33674430f4547cf6a14";

        // set up the line with the SHA provided from the file, which should then match the calculated one. Also all the other attributes used in the calculation described in the spec
        PaymentLine firstRowFromTestFile = new PaymentLine();
        firstRowFromTestFile.setSHA256(expectedSHA);

        firstRowFromTestFile.setMerchantPubKey("KKkhBNSD6pIad48vG4Er");
        firstRowFromTestFile.setPayerPubKey("lsOGMOVFH8kEGDnt1uM9");
        firstRowFromTestFile.setDebitPermissionId(BigInteger.valueOf(7922750));
        firstRowFromTestFile.setDueEpoc(1578268801L);
        firstRowFromTestFile.setAmount(new BigDecimal("64.40"));
        firstRowFromTestFile.setDueUTC(LocalDateTime.ofEpochSecond(firstRowFromTestFile.getDueEpoc(), 0, ZoneOffset.UTC)); // avoids date equality validation error

        String message = null;
        try {
            validator.validate(firstRowFromTestFile);
        } catch (ValidationException e) {
            message = e.getMessage();
        }
        assertNull(message);
    }
}