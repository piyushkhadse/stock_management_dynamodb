package com.stockmarket.stock_management.command;

import com.stockmarket.core_d.command.Command;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddStockPrice extends Command {

    private String companyName;
    private String companyCode;
    private Double stockPrice;

    public AddStockPrice() {
        super(UUID.randomUUID().toString(), "STOCK");
    }

    public AddStockPrice(String companyCode, Double stockPrice, String aggregateId) {
        super(aggregateId, "STOCK");
        this.companyCode = companyCode;
        this.stockPrice = stockPrice;
    }
}
