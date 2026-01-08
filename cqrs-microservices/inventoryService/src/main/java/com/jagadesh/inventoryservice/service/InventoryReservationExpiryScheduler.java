package com.jagadesh.inventoryservice.service;

import com.jagadesh.inventoryservice.repository.StockReservationRepo;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@EnableScheduling
@Component
public class InventoryReservationExpiryScheduler {
    private StockReservationRepo stockReservationRepo;
    private InventoryEventPublisher inventoryEventPublisher;

    public InventoryReservationExpiryScheduler(StockReservationRepo stockReservationRepo, InventoryEventPublisher inventoryEventPublisher) {
        this.stockReservationRepo = stockReservationRepo;
        this.inventoryEventPublisher = inventoryEventPublisher;
    }

    @Scheduled(fixedRate = 120_000)
    public void checkExpiredReservations() {
        Instant now = Instant.now();
        List<String> expiredOrderIds = stockReservationRepo.findDistinctOrderIdsByExpiresAtBefore(now);
        for(String orderId : expiredOrderIds) {
            inventoryEventPublisher.publishStockReservationExpiredEvent(orderId,now);
        }
    }
}
