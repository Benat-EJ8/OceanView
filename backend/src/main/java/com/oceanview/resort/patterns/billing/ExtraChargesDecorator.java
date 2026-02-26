package com.oceanview.resort.patterns.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


 //Decorator: Add extra charges to base invoice.

public class ExtraChargesDecorator implements InvoiceCharge {
    private final InvoiceCharge base;
    private final List<InvoiceCharge> extras = new ArrayList<>();

    public ExtraChargesDecorator(InvoiceCharge base) {
        this.base = base;
    }

    public void addExtra(InvoiceCharge charge) {
        if (charge != null) extras.add(charge);
    }

    @Override
    public String getDescription() {
        return base.getDescription();
    }

    @Override
    public BigDecimal getAmount() {
        BigDecimal total = base.getAmount();
        for (InvoiceCharge e : extras) {
            total = total.add(e.getAmount());
        }
        return total;
    }
}
