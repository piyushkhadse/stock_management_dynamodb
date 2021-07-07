package com.stockmarket.stock_management.domain;

import java.time.Instant;
import java.util.Comparator;

public class StockComparator implements Comparator<Stock> {
    @Override
    public int compare(Stock o1, Stock o2) {
        Instant i1 = Instant.parse(o1.getCreatedDate());
        Instant i2 = Instant.parse(o2.getCreatedDate());
        return i1.compareTo(i2);
    }
}
