package com.stockmarket.stock_management.service.impl;


import com.stockmarket.core_d.events.StockPriceAddedEvent;
import com.stockmarket.core_d.events.publisher.KafkaPublisher;
import com.stockmarket.core_d.logger.StockMarketApplicationLogger;
import com.stockmarket.stock_management.domain.StockLocalEventStore;
import com.stockmarket.stock_management.repository.StockLocalEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class LocalEventStoreScheduler {

    @Autowired
    private StockLocalEventRepository repository;

    @Autowired
    private KafkaPublisher publisher;

    @Value("${stock.management.aggregateUpdate.topic}")
    private String aggregateUpdateTopic;

    @Value("${stock.market.topic}")
    private String marketTopic;

    StockMarketApplicationLogger logger = StockMarketApplicationLogger.getLogger(this.getClass());

    /**
     * scheduler for publishing events & synchronization between localEventStore & stocks collections
     */
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void processLocalEvents() {
        List<StockLocalEventStore> list = repository.findBySent(false);
        for (int i = 0; i < list.size(); i++) {
            publishAndUpdateLocalEvents(list.get(i));
        }
    }

    /**
     * publishes evens on topics
     *
     * @param localEventStore
     * @return
     */
    private boolean publishAndUpdateLocalEvents(StockLocalEventStore localEventStore) {
        try {
            CompletableFuture<Boolean> publishedEventOnTopic = publishEvents(marketTopic, localEventStore.getEvent());
            CompletableFuture<Boolean> publishedEventOnStockAggregateTopic = publishEvents(aggregateUpdateTopic, localEventStore.getEvent());
            if (Boolean.TRUE.equals(publishedEventOnTopic.get()) && Boolean.TRUE.equals(publishedEventOnStockAggregateTopic.get())) {
                localEventStore.setSent(Boolean.TRUE);
                repository.save(localEventStore);
                return true;
            }
        } catch (Exception e) {
            logger.error().log("Exception while publishing and updating local events for localEvent:{}", localEventStore, e);
        }
        return false;
    }

    @Async
    private CompletableFuture<Boolean> publishEvents(String topic, StockPriceAddedEvent event) {
        return CompletableFuture.completedFuture(publisher.publish(topic, event));
    }
}
