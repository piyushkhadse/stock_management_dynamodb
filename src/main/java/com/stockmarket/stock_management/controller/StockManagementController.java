package com.stockmarket.stock_management.controller;

import com.stockmarket.core_d.logger.StockMarketApplicationLogger;
import com.stockmarket.stock_management.command.AddStockPrice;
import com.stockmarket.stock_management.domain.StockLocalEventStore;
import com.stockmarket.stock_management.projection.StockProjection;
import com.stockmarket.stock_management.service.StockManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StockManagementController {

    private static final String STOCK_V1_0 = "/v1.0/market/stock";

    @Autowired
    private StockManagementService stockManagementService;

    @Autowired
    private StockProjection projection;

    StockMarketApplicationLogger logger = StockMarketApplicationLogger.getLogger(this.getClass());


    @GetMapping(value = STOCK_V1_0 + "/{company_code}")
    public ResponseEntity<com.stockmarket.stock_management.projection.domain.StockProjection> getCompanyStockDetails(@PathVariable String company_code) {
        logger.info().log("Inside getCompanyStockDetails()");
        return new ResponseEntity<>(projection.getCompanyLatestStockPrice(company_code), HttpStatus.OK);
    }

    @GetMapping(value = STOCK_V1_0 + "/{company_code}/{start_date}/{end_date}")
    public ResponseEntity<List<com.stockmarket.stock_management.projection.domain.StockProjection>> getCompanyStocksDetails(@PathVariable String company_code,
                                                                                                                            @PathVariable String start_date,
                                                                                                                            @PathVariable String end_date) {
        logger.info().log("Inside getCompanyStockDetails()");
        return new ResponseEntity<>(projection.getCompanyStockPrices(company_code, start_date, end_date), HttpStatus.OK);
    }

    @PostMapping(value = STOCK_V1_0 + "/add/{company_code}")
    @PreAuthorize("hasRole('ROLE_A')")
    public ResponseEntity<StockLocalEventStore> addStockPrice(@PathVariable String company_code, @RequestBody AddStockPrice addStockPrice) {
        logger.info().log("Inside addStockPrice()");
        StockLocalEventStore response = stockManagementService.addNewStockPrice(company_code, addStockPrice);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = STOCK_V1_0 + "/delete/{company_code}")
    @PreAuthorize("hasRole('ROLE_A')")
    public ResponseEntity<Void> deleteStockPrice(@PathVariable String company_code) {
        logger.info().log("Inside deleteStockPrice()");
        stockManagementService.deleteStockPrice(company_code);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
