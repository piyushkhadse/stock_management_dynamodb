package com.stockmarket.stock_management.service;


import com.stockmarket.stock_management.command.AddStockPrice;

public interface Synchronization {

    void stockPriceAdded(AddStockPrice addStockPrice);

    void deleteStockPriceForCompany(String companyCode);

}
