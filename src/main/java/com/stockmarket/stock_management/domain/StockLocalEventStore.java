package com.stockmarket.stock_management.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.stockmarket.core_d.events.StockPriceAddedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@DynamoDBTable(tableName="stockLocalEventStore")
@AllArgsConstructor
public class StockLocalEventStore {
    @DynamoDBHashKey
    private String _id;
    @DynamoDBAttribute
    private StockPriceAddedEvent event;
    @DynamoDBAttribute
    private Boolean sent;
    @DynamoDBAttribute
    private String createdDate;

    public StockLocalEventStore(StockPriceAddedEvent event) {
        this._id = UUID.randomUUID().toString();
        this.event = event;
        this.sent = Boolean.FALSE;
        this.createdDate = Instant.now().toString();
    }

    public StockLocalEventStore() {
        this._id = UUID.randomUUID().toString();
        this.sent = Boolean.FALSE;
        this.createdDate = Instant.now().toString();
    }
}
