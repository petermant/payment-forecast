package com.pete.payment.dataload.dto;

import org.springframework.batch.item.ItemCountAware;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class PaymentLine implements ItemCountAware {

    private Date receivedUTC;
    private BigInteger merchantID;
    private String merchantName;
    private String merchantPubKey;
    private BigInteger payerID;
    private String payerPubKey;
    private BigInteger debitPermissionId;
    private Date dueUTC;
    private Long dueEpoc;
    private Currency currency;
    private BigDecimal amount;

    private String SHA256;
    private int itemCount;

    public Date getReceivedUTC() {
        return receivedUTC;
    }

    public void setReceivedUTC(Date receivedUTC) {
        this.receivedUTC = receivedUTC;
    }

    public BigInteger getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(BigInteger merchantID) {
        this.merchantID = merchantID;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantPubKey() {
        return merchantPubKey;
    }

    public void setMerchantPubKey(String merchantPubKey) {
        this.merchantPubKey = merchantPubKey;
    }

    public BigInteger getPayerID() {
        return payerID;
    }

    public void setPayerID(BigInteger payerID) {
        this.payerID = payerID;
    }

    public String getPayerPubKey() {
        return payerPubKey;
    }

    public void setPayerPubKey(String payerPubKey) {
        this.payerPubKey = payerPubKey;
    }

    public BigInteger getDebitPermissionId() {
        return debitPermissionId;
    }

    public void setDebitPermissionId(BigInteger debitPermissionId) {
        this.debitPermissionId = debitPermissionId;
    }

    public Date getDueUTC() {
        return dueUTC;
    }

    public void setDueUTC(Date dueUTC) {
        this.dueUTC = dueUTC;
    }

    public Long getDueEpoc() {
        return dueEpoc;
    }

    public void setDueEpoc(Long dueEpoc) {
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

    public String getSHA256() {
        return SHA256;
    }

    public void setSHA256(String SHA256) {
        this.SHA256 = SHA256;
    }

    @Override
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    @Override
    public String toString() {
        return "PaymentLine{" +
                "receivedUTC=" + receivedUTC +
                ", merchantID=" + merchantID +
                ", merchantName='" + merchantName + '\'' +
                ", merchantPubKey='" + merchantPubKey + '\'' +
                ", payerID=" + payerID +
                ", payerPubKey='" + payerPubKey + '\'' +
                ", debitPermissionId=" + debitPermissionId +
                ", dueUTC=" + dueUTC +
                ", dueEpoc=" + dueEpoc +
                ", currency=" + currency +
                ", amount=" + amount +
                ", SHA256='" + SHA256 + '\'' +
                ", itemCount=" + itemCount +
                '}';
    }
}
