package com.oceanview.resort.patterns.billing;

import java.math.BigDecimal;

/**
 * Decorator base: Invoice charge (room + extras).
 */
public interface InvoiceCharge {
    String getDescription();
    BigDecimal getAmount();
}
