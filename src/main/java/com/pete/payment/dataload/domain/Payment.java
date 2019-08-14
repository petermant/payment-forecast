package com.pete.payment.dataload.domain;

import com.pete.payment.dataload.dto.Currency;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private BigInteger id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @NotNull
    private Merchant merchant;

    @ManyToOne(cascade = CascadeType.MERGE)
    @NotNull
    private Payer payer;

    @Column
    @NotNull
    private LocalDateTime receivedUTC;

    @Column
    @NotNull
    private BigInteger debitPermissionId;

    @Column
    @NotNull
    private LocalDateTime dueEpoc;

    @Column
    @NotNull
    private Currency currency;

    @Column
    @NotNull
    private BigDecimal amount;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public LocalDateTime getReceivedUTC() {
        return receivedUTC;
    }

    public void setReceivedUTC(LocalDateTime receivedUTC) {
        this.receivedUTC = receivedUTC;
    }

    public BigInteger getDebitPermissionId() {
        return debitPermissionId;
    }

    public void setDebitPermissionId(BigInteger debitPermissionId) {
        this.debitPermissionId = debitPermissionId;
    }

    public LocalDateTime getDueEpoc() {
        return dueEpoc;
    }

    public void setDueEpoc(LocalDateTime dueEpoc) {
        this.dueEpoc = dueEpoc;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id.equals(payment.id) &&
                merchant.equals(payment.merchant) &&
                payer.equals(payment.payer) &&
                receivedUTC.equals(payment.receivedUTC) &&
                debitPermissionId.equals(payment.debitPermissionId) &&
                dueEpoc.equals(payment.dueEpoc) &&
                currency == payment.currency &&
                amount.equals(payment.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, merchant, payer, receivedUTC, debitPermissionId, dueEpoc, currency, amount);
    }
}
