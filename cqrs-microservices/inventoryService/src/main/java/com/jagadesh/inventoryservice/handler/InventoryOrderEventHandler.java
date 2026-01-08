package com.jagadesh.inventoryservice.handler;

import com.jagadesh.event.inventory.ReleaseStockCommand;
import com.jagadesh.event.inventory.StockReleasedEvent;
import com.jagadesh.event.order.OrderConfirmedEvent;
import com.jagadesh.event.order.OrderCreatedEvent;
import com.jagadesh.event.order.OrderItemPayload;
import com.jagadesh.inventoryservice.dto.StockAvailabilityStatus;
import com.jagadesh.inventoryservice.model.ProcessedEvent;
import com.jagadesh.inventoryservice.model.StockReservation;
import com.jagadesh.inventoryservice.repository.ProcessedEventRepo;
import com.jagadesh.inventoryservice.service.InventoryEventPublisher;
import com.jagadesh.inventoryservice.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@KafkaListener(
        topics = "order-event",
        groupId = "inventory-group"
)
@Service
@Slf4j
public class InventoryOrderEventHandler {

    private InventoryService inventoryService;
    private InventoryEventPublisher inventoryEventPublisher;
    private ProcessedEventRepo processedEventRepo;

    public InventoryOrderEventHandler(
            InventoryService inventoryService, InventoryEventPublisher inventoryEventPublisher, ProcessedEventRepo processedEventRepo) {
        this.inventoryService = inventoryService;
        this.inventoryEventPublisher = inventoryEventPublisher;
        this.processedEventRepo = processedEventRepo;
    }

    @KafkaHandler
    @Transactional
    public void handleOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        if(processedEventRepo.existsById(orderCreatedEvent.getEventId())) {
            return;
        }

        for(OrderItemPayload item: orderCreatedEvent.getItemInfo()) {
            StockAvailabilityStatus status = inventoryService.checkStockAvailability(item.getProductId(), item.getQuantity());
            if(status.equals(StockAvailabilityStatus.FAILED)) {
                log.info("eventId: {} orderId: {} eventType: {} action: STOCK_CHECK for productId: {} status: FAILED",
                        orderCreatedEvent.getEventId(),
                        orderCreatedEvent.getOrderId(),
                        "orderCreatedEvent",
                        item.getProductId()
                );
                inventoryEventPublisher.publishStockReservationFailedEvent(
                        orderCreatedEvent.getOrderId(),
                        item.getProductId(),
                        item.getQuantity(),
                        "Insufficient stock for productId: " + item.getProductId());
                return;
            } else {
                log.info("eventId: {} orderId: {} eventType: {} action: STOCK_CHECK for productId: {} status: SUCCESS",
                        orderCreatedEvent.getEventId(),
                        orderCreatedEvent.getOrderId(),
                        "orderCreatedEvent",
                        item.getProductId()
                );
            }
        }

        String orderId = orderCreatedEvent.getOrderId();
        for(OrderItemPayload item: orderCreatedEvent.getItemInfo()) {
            inventoryService.reserveStock(orderId, item.getProductId(), item.getQuantity());
            log.info("eventId: {} orderId: {} eventType: {} action: STOCK_RESERVED for productId: {} status: SUCCESS",
                    orderCreatedEvent.getEventId(),
                    orderCreatedEvent.getOrderId(),
                    "orderCreatedEvent",
                    item.getProductId()
            );
        }

        processedEventRepo.save(new com.jagadesh.inventoryservice.model.ProcessedEvent(orderCreatedEvent.getEventId()));
        inventoryEventPublisher.publishStockReservedEvent(orderCreatedEvent.getOrderId(), orderCreatedEvent.getTotalCost());
    }

    @KafkaHandler
    @Transactional
    public void handleReleaseStockCommand(ReleaseStockCommand releaseStockCommand) {
        if(processedEventRepo.existsById(releaseStockCommand.getEventId())) {
            return;
        }

        inventoryService.releaseStock(releaseStockCommand.getOrderId());
        inventoryEventPublisher.publishStockReleasedEvent(releaseStockCommand.getOrderId());

        processedEventRepo.save(new com.jagadesh.inventoryservice.model.ProcessedEvent(releaseStockCommand.getEventId()));
        log.info("eventId: {} orderId: {} eventType: {} action: STOCK_RELEASED status: SUCCESS",
                releaseStockCommand.getEventId(),
                releaseStockCommand.getOrderId(),
                "releaseStockCommand"
        );
    }

    @KafkaHandler
    @Transactional
    public void handleOrderConfirmedEvent(OrderConfirmedEvent orderConfirmedEvent) {
        if(processedEventRepo.existsById(orderConfirmedEvent.getEventId())) {
            return;
        }

        inventoryService.consumeReservedStocks(orderConfirmedEvent.getOrderId());

        processedEventRepo.save(new ProcessedEvent(orderConfirmedEvent.getEventId()));
        log.info("eventId: {} orderId: {} eventType: {} action: RESERVED_STOCK_CONSUMED status: SUCCESS",
                orderConfirmedEvent.getEventId(),
                orderConfirmedEvent.getOrderId(),
                "orderConfirmedEvent"
        );
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("Unknown order event received by inventory service: {}", event);
    }
}
