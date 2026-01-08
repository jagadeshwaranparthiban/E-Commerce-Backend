package com.jagadesh.inventoryservice.handler;

import com.jagadesh.event.inventory.StockReservationExpiredEvent;
import com.jagadesh.inventoryservice.model.Inventory;
import com.jagadesh.inventoryservice.model.ProcessedEvent;
import com.jagadesh.inventoryservice.model.StockReservation;
import com.jagadesh.inventoryservice.repository.InventoryRepo;
import com.jagadesh.inventoryservice.repository.ProcessedEventRepo;
import com.jagadesh.inventoryservice.repository.StockReservationRepo;
import com.jagadesh.inventoryservice.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@KafkaListener(
        topics = "inventory-event",
        groupId = "inventory-group"
)
@Service
@Slf4j
public class InventoryEventHandler {
    private StockReservationRepo stockReservationRepo;
    private InventoryRepo inventoryRepo;
    private InventoryService inventoryService;
    private ProcessedEventRepo processedEventRepo;

    public InventoryEventHandler(
            StockReservationRepo stockReservationRepo, InventoryRepo inventoryRepo, InventoryService inventoryService, ProcessedEventRepo processedEventRepo) {
        this.stockReservationRepo = stockReservationRepo;
        this.inventoryRepo = inventoryRepo;
        this.inventoryService = inventoryService;
        this.processedEventRepo = processedEventRepo;
    }

    @KafkaHandler
    @Transactional
    public void handleStockReservationExpiredEvent(StockReservationExpiredEvent stockReservationExpiredEvent) {
        if(processedEventRepo.existsById(stockReservationExpiredEvent.getEventId())) {
            return;
        }

        inventoryService.releaseStock(stockReservationExpiredEvent.getOrderId());
        processedEventRepo.save(new ProcessedEvent(stockReservationExpiredEvent.getEventId()));

        log.info("eventId: {} orderId: {} eventType: {} action: PROCESSED status: SUCCESS",
                stockReservationExpiredEvent.getEventId(),
                stockReservationExpiredEvent.getOrderId(),
                "stockReservationExpiredEvent"
        );
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("Unknown inventory event received by inventory service: {}", event);
    }
}
