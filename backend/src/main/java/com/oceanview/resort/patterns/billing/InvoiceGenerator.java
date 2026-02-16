package com.oceanview.resort.patterns.billing;

import java.math.BigDecimal;

/**
 * Template Method: Invoice generation steps.
 */
public abstract class InvoiceGenerator {
    public final String generate(String reservationRef, BigDecimal subtotal, BigDecimal taxRate, BigDecimal discount) {
        BigDecimal tax = calculateTax(subtotal, taxRate);
        BigDecimal total = subtotal.add(tax).subtract(discount != null ? discount : BigDecimal.ZERO);
        return buildInvoice(reservationRef, subtotal, tax, discount, total);
    }

    protected BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        if (subtotal == null || taxRate == null) return BigDecimal.ZERO;
        return subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    protected abstract String buildInvoice(String reservationRef, BigDecimal subtotal, BigDecimal tax, BigDecimal discount, BigDecimal total);
}
