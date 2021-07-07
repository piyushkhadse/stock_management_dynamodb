package com.stockmarket.stock_management.domain;

import java.time.Instant;
import java.util.Comparator;

public class StockLocalEventStoreComparator implements Comparator<StockLocalEventStore> {
    @Override
    public int compare(StockLocalEventStore o1, StockLocalEventStore o2) {
        Instant i1 = Instant.parse(o1.getCreatedDate());
        Instant i2 = Instant.parse(o2.getCreatedDate());
        return i1.compareTo(i2);
    }
}
