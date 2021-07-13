package com.stockmarket.stock_management.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.stockmarket.core_d.domain.AggregateRoot;
import com.stockmarket.core_d.domain.Error;
import com.stockmarket.core_d.events.StockPriceAddedEvent;
import com.stockmarket.stock_management.command.AddStockPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@DynamoDBTable(tableName="stocks")
@NoArgsConstructor
@AllArgsConstructor
public class Stock extends AggregateRoot {
    @DynamoDBHashKey
    private String _id;
    @DynamoDBAttribute
    private String companyCode;
    @DynamoDBAttribute
    private Double stockPrice;
    @DynamoDBAttribute
    private String createdDate;

    public Stock(AddStockPrice addStockPrice) {
        validate(addStockPrice);
        if (addStockPrice.getErrors().isEmpty()) {
            this._id = addStockPrice.getAggregateId();
            this.companyCode = addStockPrice.getCompanyCode();
            this.stockPrice = addStockPrice.getStockPrice();
            this.createdDate = Instant.now().toString();
            StockPriceAddedEvent event = new StockPriceAddedEvent(
                    this._id, addStockPrice.getAggregateType(),
                    this.companyCode, stockPrice);
            this.registerEvent(event);
        }
    }

    public void validate(AddStockPrice addStockPrice) {
        List<Error> errors = new ArrayList<>();
        String INVALID_INPUT = "INVALID_INPUT";
        if (mandatoryCheck(addStockPrice.getCompanyCode())) {
            errors.add(new Error(INVALID_INPUT, "companyCode is invalid input"));
            addStockPrice.setErrors(errors);
        } else if (addStockPrice.getStockPrice() == null) {
            errors.add(new Error(INVALID_INPUT, "stockPrice is invalid input"));
            addStockPrice.setErrors(errors);
        } else if (addStockPrice.getStockPrice().compareTo(0.0d) < 0) {
            errors.add(new Error(INVALID_INPUT, "stockPrice is invalid input. It should be greater than 0."));
            addStockPrice.setErrors(errors);
        }
    }

    private boolean mandatoryCheck(String field) {
        return field == null || field.isEmpty();
    }
}
