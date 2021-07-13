package com.stockmarket.stock_management.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.stockmarket.stock_management.domain.StockLocalEventStore;
import com.stockmarket.stock_management.domain.StockLocalEventStoreComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockLocalEventRepository {

    @Autowired
    DynamoDBMapper dynamoDBMapper;

    public StockLocalEventStore save(StockLocalEventStore stockLocalEventStore) {
        dynamoDBMapper.save(stockLocalEventStore);
        return stockLocalEventStore;
    }

    public List<StockLocalEventStore> findByEvent_CompanyCode(String companyCode) {
        List<StockLocalEventStore> resp = new ArrayList<>();
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            if (companyCode != null) {
                Map<String, AttributeValue> map = new HashMap<>();
                map.put(":companyCode", new AttributeValue().withS(companyCode));
                scanExpression.withFilterExpression("event.companyCode = :companyCode")
                .withExpressionAttributeValues(map);
                resp = dynamoDBMapper.scan(StockLocalEventStore.class, scanExpression);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }


    public List<StockLocalEventStore> findBySent(Boolean isSent) {
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            if (isSent != null) {
                scanExpression.addFilterCondition("sent",
                        new Condition()
                                .withComparisonOperator(ComparisonOperator.EQ)
                                .withAttributeValueList(new AttributeValue().withN(String.valueOf(isSent ? 1 : 0))));

                List<StockLocalEventStore> resp = dynamoDBMapper.scan(StockLocalEventStore.class, scanExpression);
                if(resp.isEmpty()) {
                  return resp;
                }
                List<StockLocalEventStore> limitedRecords = new ArrayList<>();
                List<StockLocalEventStore> temp = new ArrayList<>(resp);
                Collections.sort(temp, new StockLocalEventStoreComparator());
                int size = temp.size()>5 ? 5 : temp.size();
                for (int i=0; i<size; i++) {
                    limitedRecords.add(temp.get(i));
                }
                return limitedRecords;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public void deleteAllByEvent_CompanyCode(String companyCode) {
        List<StockLocalEventStore> stockLocalEventStoreList = findByEvent_CompanyCode(companyCode);
        for (int i=0; i<stockLocalEventStoreList.size(); i++) {
            dynamoDBMapper.delete(stockLocalEventStoreList.get(i));
        }
    }

}
