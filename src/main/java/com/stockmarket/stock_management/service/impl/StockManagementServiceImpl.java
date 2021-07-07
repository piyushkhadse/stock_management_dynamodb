package com.stockmarket.stock_management.service.impl;

import com.stockmarket.core_d.domain.Error;
import com.stockmarket.core_d.events.StockPriceAddedEvent;
import com.stockmarket.core_d.events.StockPriceDeletedEvent;
import com.stockmarket.core_d.events.publisher.KafkaPublisher;
import com.stockmarket.core_d.exception.ApplicationException;
import com.stockmarket.core_d.logger.StockMarketApplicationLogger;
import com.stockmarket.stock_management.command.AddStockPrice;
import com.stockmarket.stock_management.domain.CompanyView;
import com.stockmarket.stock_management.domain.Stock;
import com.stockmarket.stock_management.domain.StockLocalEventStore;
import com.stockmarket.stock_management.projection.repository.StockProjectionRepository;
import com.stockmarket.stock_management.repository.StockLocalEventRepository;
import com.stockmarket.stock_management.service.StockManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockManagementServiceImpl implements StockManagementService {
    @Autowired
    private KafkaPublisher publisher;

    @Autowired
    private StockLocalEventRepository stockLocalEventRepository;

    @Autowired
    private StockProjectionRepository stockProjectionRepository;

    @Autowired
    private InternalServiceCall internalServiceCall;

    @Value("${stock.management.aggregateUpdate.topic}")
    private String stockManagementAggregateUpdateTopic;

    StockMarketApplicationLogger logger = StockMarketApplicationLogger.getLogger(this.getClass());

    /**
     * adds new stock price for a company
     *
     * @param companyCode
     * @param addStockPrice
     * @return
     */
    @Override
    public StockLocalEventStore addNewStockPrice(String companyCode, AddStockPrice addStockPrice) {
        try {
            Stock stock = new Stock(addStockPrice);
            if (!addStockPrice.getErrors().isEmpty()) {
                throw new ApplicationException(addStockPrice.getErrors().get(0), 400);
            }
            CompanyView response = internalServiceCall.getCompanyDetails(companyCode);
            if (response != null && response.getCompanyCode().equalsIgnoreCase(addStockPrice.getCompanyCode())) {
                return stockLocalEventRepository.save(new StockLocalEventStore((StockPriceAddedEvent) stock.events().get(0)));
            } else {
                throw new ApplicationException(new Error("INVALID_INPUT", "companyCode is invalid"), 400);
            }
        } catch (ApplicationException exception) {
            logger.error().log("Error in addNewStockPrice() to add stock price: {} for company code: {}", addStockPrice.getStockPrice(), companyCode, exception);
            throw exception;
        } catch (Exception e) {
            logger.error().log("Error in addNewStockPrice() to add stock price: {} for company code: {}", addStockPrice.getStockPrice(), companyCode, e);
            throw new ApplicationException(new Error("INTERNAL_SERVER_ERROR", "Internal Server Error"), 500);
        }
    }

    /**
     * deletes stock prices of a company
     *
     * @param companyCode
     */
    @Override
    public void deleteStockPrice(String companyCode) {
        try {
            List<StockLocalEventStore> list = stockLocalEventRepository.findByEvent_CompanyCode(companyCode);
            if (list != null && !list.isEmpty()) {
                StockPriceDeletedEvent event = new StockPriceDeletedEvent(
                        null, "STOCK", companyCode
                );
                stockLocalEventRepository.deleteAllByEvent_CompanyCode(companyCode);
                publisher.publish(event);
                publisher.publish(stockManagementAggregateUpdateTopic, event);
            } else {
                logger.info().log("Stock prices are not available for companyCode:{}", companyCode);
            }
        } catch (Exception e) {
            logger.error().log("Error in deleteStockPrice() to delete stock prices of company code: {}", companyCode, e);
            throw new ApplicationException(new Error("INTERNAL_SERVER_ERROR", "Internal Server Error"), 500);
        }
    }
}
