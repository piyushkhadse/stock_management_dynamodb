package com.stockmarket.stock_management.service.impl;

import com.stockmarket.core_d.logger.StockMarketApplicationLogger;
import com.stockmarket.stock_management.command.AddStockPrice;
import com.stockmarket.stock_management.domain.Stock;
import com.stockmarket.stock_management.projection.repository.StockProjectionRepository;
import com.stockmarket.stock_management.service.Synchronization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SynchronizationImpl implements Synchronization {

    @Autowired
    private StockProjectionRepository repository;

    StockMarketApplicationLogger logger = StockMarketApplicationLogger.getLogger(this.getClass());

    /**
     * add stock price in a stocks collection
     *
     * @param addStockPrice
     */
    @Override
    public void stockPriceAdded(AddStockPrice addStockPrice) {
        try {
            Stock stock = new Stock(addStockPrice);
            repository.save(stock);
        } catch (Exception e) {
            logger.error().log("Error while adding stock price for companyCode:{}", addStockPrice.getCompanyCode(), e);
        }
    }

    /**
     * deletes stock prices of company from stocks collection
     *
     * @param companyCode
     */
    @Override
    public void deleteStockPriceForCompany(String companyCode) {
        try {
            List<Stock> list = repository.findByCompanyCode(companyCode);
            if (list != null && !list.isEmpty()) {
                repository.deleteAllByCompanyCode(companyCode);
            } else {
                logger.error().log("No stock prices are found for companyCode:{}", companyCode);
            }
        } catch (Exception e) {
            logger.error().log("Error while deleting all stock prices of companyCode:{}", companyCode, e);
        }
    }
}
