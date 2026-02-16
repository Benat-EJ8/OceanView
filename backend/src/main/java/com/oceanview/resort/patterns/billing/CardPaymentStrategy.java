package com.oceanview.resort.patterns.billing;

import java.math.BigDecimal;

public class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public String getTypeCode() {
        return "CARD";
    }

    @Override
    public boolean process(BigDecimal amount, String reference) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
