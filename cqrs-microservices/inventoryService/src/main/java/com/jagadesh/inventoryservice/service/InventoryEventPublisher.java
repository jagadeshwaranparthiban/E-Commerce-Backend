package com.jagadesh.inventoryservice.service;

import com.jagadesh.event.inventory.*;
import com.jagadesh.inventoryservice.model.Inventory;
import com.jagadesh.inventoryservice.repository.InventoryRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class InventoryEventPublisher {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private InventoryRepo inventoryRepo;

    public InventoryEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, InventoryRepo inventoryRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryRepo = inventoryRepo;
    }

    private void logEventPublisher(String eventId, long productId, String eventType) {
        log.info("eventId: {} productId: {} eventType: {} action: PUBLISHED", eventId, productId, eventType);
    }

    public void publishStockAddedEvent(StockAddedEvent stockAddedEvent) {
        Inventory inventory = inventoryRepo.findById(stockAddedEvent.getProductId()).get();

        inventory.increaseAvailableQuantity(stockAddedEvent.getQuantity());
        inventoryRepo.save(inventory);

        kafkaTemplate.send(
                "inventory-event",
                String.valueOf(stockAddedEvent.getProductId()),
                stockAddedEvent
        );

        logEventPublisher(stockAddedEvent.getEventId(), stockAddedEvent.getProductId(), "stockAddedEvent");
    }

    public void publishStockReservedEvent(String orderId, BigDecimal totalCost) {
        StockReservedEvent stockReservedEvent = new StockReservedEvent(
                UUID.randomUUID().toString(),
                orderId,
                totalCost,
                Instant.now()
        );
        kafkaTemplate.send(
                "inventory-event",
                orderId,
                stockReservedEvent
        );

        log.info("eventId: {} orderId: {} eventType: {} action: STOCK_RESERVED status: PUBLISHED",
                stockReservedEvent.getEventId(),
                stockReservedEvent.getOrderId(),
                "stockReservedEvent"
        );
    }

    public void publishStockReservationFailedEvent(String orderId, long productId, int quantity, String reason) {
        StockReservationFailedEvent stockReservationFailedEvent = new StockReservationFailedEvent(
                UUID.randomUUID().toString(),
                orderId,
                productId,
                quantity,
                reason,
                Instant.now()
        );
        kafkaTemplate.send(
                "inventory-event",
                String.valueOf(orderId),
                stockReservationFailedEvent
        );
        log.info("eventId: {} orderId: {} eventType: {} action: STOCK_RESERVATION_FAILED for productId: {} status: PUBLISHED",
                stockReservationFailedEvent.getEventId(),
                stockReservationFailedEvent.getOrderId(),
                "stockReservationFailedEvent",
                stockReservationFailedEvent.getProductId()
        );
    }

    public void publishStockReleasedEvent(String orderId) {
        StockReleasedEvent stockReleasedEvent = new StockReleasedEvent(
                UUID.randomUUID().toString(),
                orderId,
                Instant.now()
        );
        kafkaTemplate.send(
                "inventory-event",
                orderId,
                stockReleasedEvent
        );
        log.info("eventId: {} orderId: {} evenType: {} action: STOCK_RELEASED status: PUBLISHED",
                stockReleasedEvent.getEventId(),
                stockReleasedEvent.getOrderId(),
                "stockReleasedEvent"
        );
    }

    public void publishStockReservationExpiredEvent(String orderId, Instant expiredAt) {
        StockReservationExpiredEvent stockReservationExpiredEvent = new StockReservationExpiredEvent(
                UUID.randomUUID().toString(),
                orderId,
                expiredAt
        );
        kafkaTemplate.send(
                "inventory-event",
                orderId,
                stockReservationExpiredEvent
        );
        log.info("eventId: {} orderId: {} evenType: {} action: STOCK_RESERVATION_EXPIRED status: PUBLISHED",
                stockReservationExpiredEvent.getEventId(),
                stockReservationExpiredEvent.getOrderId(),
                "stockReservationExpiredEvent"
        );
    }
}
