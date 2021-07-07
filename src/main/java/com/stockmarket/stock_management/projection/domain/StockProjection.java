package com.stockmarket.stock_management.projection.domain;

import com.stockmarket.stock_management.domain.Stock;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockProjection {
    private String companyCode;
    private Double stockPrice;
    private String createdDate;

    public StockProjection(Stock stock) {
        this.companyCode = stock.getCompanyCode();
        this.stockPrice = stock.getStockPrice();
        this.createdDate = stock.getCreatedDate();
    }
}
