package com.stockmarket.stock_management.service;

import com.stockmarket.stock_management.command.AddStockPrice;
import com.stockmarket.stock_management.domain.StockLocalEventStore;

public interface StockManagementService {
    StockLocalEventStore addNewStockPrice(String companyCode, AddStockPrice addStockPrice);

    void deleteStockPrice(String companyCode);
}
