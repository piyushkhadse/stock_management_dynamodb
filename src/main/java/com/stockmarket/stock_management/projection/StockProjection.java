package com.stockmarket.stock_management.projection;

import com.stockmarket.core_d.domain.Error;
import com.stockmarket.core_d.exception.ApplicationException;
import com.stockmarket.core_d.logger.StockMarketApplicationLogger;
import com.stockmarket.stock_management.domain.Stock;
import com.stockmarket.stock_management.projection.repository.StockProjectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StockProjection {

    @Autowired
    private StockProjectionRepository stockProjectionRepository;


    StockMarketApplicationLogger logger = StockMarketApplicationLogger.getLogger(this.getClass());

    /**
     * returns list of stocks for a specific period
     *
     * @param companyCode
     * @param startDate
     * @param endDate
     * @return
     */
    public List<com.stockmarket.stock_management.projection.domain.StockProjection> getCompanyStockPrices(String companyCode, String startDate, String endDate) {
        try {
            List<com.stockmarket.stock_management.projection.domain.StockProjection> stocksProjection = new ArrayList<>();
            List<Stock> stocks = stockProjectionRepository.getAllStockPrices(companyCode, startDate, endDate);

            for (int i = 0; i < stocks.size(); i++) {
                stocksProjection.add(new com.stockmarket.stock_management.projection.domain.StockProjection(stocks.get(i)));
            }
            return stocksProjection;
        } catch (Exception e) {
            logger.error().log("Error while fetching all stock prices for company code:{}", companyCode, e);
            throw new ApplicationException(new Error("INTERNAL_SERVER_ERROR", "Internal Server Error"), 500);
        }
    }

    /**
     * returns latest stock price of a company
     *
     * @param companyCode
     * @return
     */
    public com.stockmarket.stock_management.projection.domain.StockProjection getCompanyLatestStockPrice(String companyCode) {
        try {
            Stock stock = stockProjectionRepository.getLatestStockPrice(companyCode);
            if (stock != null) {
                return new com.stockmarket.stock_management.projection.domain.StockProjection(stock);
            } else {
                logger.info().log("No stock prices are available for companyCode:{}", companyCode);
                return null;
            }
        } catch (Exception e) {
            logger.error().log("Error while fetching all stock prices for company code:{}", companyCode, e);
            throw new ApplicationException(new Error("INTERNAL_SERVER_ERROR", "Internal Server Error"), 500);
        }
    }


}
