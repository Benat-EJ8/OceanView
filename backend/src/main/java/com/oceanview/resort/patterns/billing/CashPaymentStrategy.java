package com.oceanview.resort.patterns.billing;

import java.math.BigDecimal;

public class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public String getTypeCode() {
        return "CASH";
    }

    @Override
    public boolean process(BigDecimal amount, String reference) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
