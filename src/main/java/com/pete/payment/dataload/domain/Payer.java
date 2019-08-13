package com.pete.payment.dataload.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Objects;

@Entity
public class Payer {

    @Id
    private BigInteger id;

    @Column
    @NotNull
    private String payerPublicKey;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getPayerPublicKey() {
        return payerPublicKey;
    }

    public void setPayerPublicKey(String payerPublicKey) {
        this.payerPublicKey = payerPublicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payer payer = (Payer) o;
        return id.equals(payer.id) &&
                payerPublicKey.equals(payer.payerPublicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, payerPublicKey);
    }
}
