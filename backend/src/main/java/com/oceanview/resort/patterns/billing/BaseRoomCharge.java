package com.oceanview.resort.patterns.billing;

import java.math.BigDecimal;

public class BaseRoomCharge implements InvoiceCharge {
    private final String description;
    private final BigDecimal amount;

    public BaseRoomCharge(String description, BigDecimal amount) {
        this.description = description;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }
}
