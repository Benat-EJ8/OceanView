package com.oceanview.resort.patterns.billing;

import java.math.BigDecimal;

/**
 * Strategy: Payment type handling.
 */
public interface PaymentStrategy {
    String getTypeCode();
    boolean process(BigDecimal amount, String reference);
}
