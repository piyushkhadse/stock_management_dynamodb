package com.stockmarket.stock_management.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmarket.core_d.logger.StockMarketApplicationLogger;
import com.stockmarket.stock_management.command.AddStockPrice;
import com.stockmarket.stock_management.service.Synchronization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AggregateUpdateEventListener {

    @Value("${stock.management.aggregateUpdate.topic}")
    private String topic;

    @Value("${aggregateUpdate.consumer.group.id}")
    private String groupId;

    @Autowired
    private Synchronization synchronization;

    @Autowired
    private ObjectMapper objectMapper;

    StockMarketApplicationLogger logger = StockMarketApplicationLogger.getLogger(this.getClass());

    /**
     * event listener to process events
     *
     * @param message
     */
    @KafkaListener(topics = "stockManagementTopic")
    public void processMessage(String message) {
        logger.info().log("Inside aggregateUpdateEventListener->processMessage()", message);
        try {
            if (message instanceof String) {
                logger.info().log("processMessage -> Received - {}", message);
                JsonNode eventNode = objectMapper.readValue(message, JsonNode.class);
                logger.info().log("processedMessage -> {}", eventNode);
                if (eventNode.get("name").asText().equals("stock-price-added")) {
                    synchronization.stockPriceAdded(createPayload(eventNode));
                } else if (eventNode.get("name").asText().equals("stock-price-deleted")) {
                    synchronization.deleteStockPriceForCompany(eventNode.get("companyCode").asText());
                }
            }
        } catch (Exception e) {
            logger.error().log("processMessage -> problem while processing message ", e);
        }
    }

    private AddStockPrice createPayload(JsonNode node) {
        return new AddStockPrice(node.get("companyCode").asText(), node.get("stockPrice").asDouble(), node.get("aggregateId").asText());
    }
}
