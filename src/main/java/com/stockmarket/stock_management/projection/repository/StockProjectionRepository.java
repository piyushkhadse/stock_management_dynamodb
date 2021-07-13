package com.stockmarket.stock_management.projection.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.stockmarket.core_d.domain.Error;
import com.stockmarket.core_d.exception.ApplicationException;
import com.stockmarket.stock_management.domain.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class StockProjectionRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Stock save(Stock stock) {
        dynamoDBMapper.save(stock);
        return stock;
    }

    public List<Stock> findByCompanyCode(String companyCode) {
        List<Stock> resp = new ArrayList<>();
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            if (companyCode != null) {
                scanExpression.addFilterCondition("companyCode",
                        new Condition()
                                .withComparisonOperator(ComparisonOperator.EQ)
                                .withAttributeValueList(new AttributeValue().withS(companyCode)));

                resp = dynamoDBMapper.scan(Stock.class, scanExpression);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    public void deleteAllByCompanyCode(String companyCode) {
        List<Stock> stockList = findByCompanyCode(companyCode);
        for (int i=0; i<stockList.size(); i++) {
            dynamoDBMapper.delete(stockList.get(i));
        }
    }


    /**
     * returns list of stock prices for a specific period from database
     *
     * @param companyCode
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Stock> getAllStockPrices(String companyCode, String startDate, String endDate) {
        List<Stock> resp = new ArrayList<>();
        try {
            String fromDate = startDate + "T00:00:00:00Z";
            String toDate = endDate + "T23:59:59:00Z";
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            if (companyCode != null) {
                Map<String, AttributeValue> map = new HashMap<>();
                map.put(":companyCode", new AttributeValue().withS(companyCode));
                map.put(":fromDate",new AttributeValue().withS(fromDate));
                map.put(":toDate",new AttributeValue().withS(toDate));
                scanExpression.withFilterExpression("companyCode = :companyCode AND createdDate between :fromDate AND :toDate ")
                .withExpressionAttributeValues(map);
                resp = dynamoDBMapper.scan(Stock.class, scanExpression);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    /**
     * returns latest stock price of a company from database
     *
     * @param companyCode
     * @return
     */
    public Stock getLatestStockPrice(String companyCode) {
        try {
            List<Stock> stockList = new ArrayList<>(findByCompanyCode(companyCode));
            if(stockList.isEmpty()){
                return null;
            } else {
                Comparator<Stock> compareByCreatedDate = Comparator.comparing(Stock::getCreatedDate);
                Collections.sort(stockList, compareByCreatedDate.reversed());
                return stockList.get(0);
            }
        } catch (Exception e) {
            throw new ApplicationException(new Error("INTERNAL_SERVER_ERROR", "Internal Server Error"), 500);
        }
    }
}
