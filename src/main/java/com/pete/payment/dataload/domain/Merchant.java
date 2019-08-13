package com.pete.payment.dataload.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Objects;

@Entity
public class Merchant {

    @Id
    @NotNull
    private BigInteger id;

    @Column
    @NotNull
    private String merchantName;

    @Column
    @NotNull
    private String publicKey;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Merchant merchant = (Merchant) o;
        return id.equals(merchant.id) &&
                merchantName.equals(merchant.merchantName) &&
                publicKey.equals(merchant.publicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, merchantName, publicKey);
    }
}
