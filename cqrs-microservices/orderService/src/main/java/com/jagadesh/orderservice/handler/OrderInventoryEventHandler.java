package com.jagadesh.orderservice.handler;

import com.jagadesh.event.inventory.StockReservationExpiredEvent;
import com.jagadesh.event.inventory.StockReservationFailedEvent;
import com.jagadesh.event.inventory.StockReservedEvent;
import com.jagadesh.orderservice.model.Order;
import com.jagadesh.orderservice.model.OrderStatus;
import com.jagadesh.orderservice.model.ProcessedEvent;
import com.jagadesh.orderservice.repository.OrderRepo;
import com.jagadesh.orderservice.repository.ProcessedEventRepo;
import com.jagadesh.orderservice.service.OrderEventPublisher;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@KafkaListener(
        topics = "inventory-event",
        groupId = "order-group"
)
@Service
@Slf4j
public class OrderInventoryEventHandler {

    private OrderRepo orderRepo;
    private ProcessedEventRepo processedEventRepo;
    private OrderEventPublisher orderEventPublisher;

    public OrderInventoryEventHandler(OrderRepo orderRepo, ProcessedEventRepo processedEventRepo, OrderEventPublisher orderEventPublisher) {
        this.orderRepo = orderRepo;
        this.processedEventRepo = processedEventRepo;
        this.orderEventPublisher = orderEventPublisher;
    }

    @KafkaHandler
    @Transactional
    public void handleStockReservationExpiredEvent(StockReservationExpiredEvent stockReservationExpiredEvent) {
        if(processedEventRepo.existsById(stockReservationExpiredEvent.getOrderId())) {
            return;
        }

        log.info("eventId: {} orderId: {} evenType: {} action: RECEIVED",
                stockReservationExpiredEvent.getEventId(),
                stockReservationExpiredEvent.getOrderId(),
                "stockReservationExpiredEvent"
        );

        Order order = orderRepo.findById(stockReservationExpiredEvent.getOrderId()).get();

        if(!order.isFinal()) {
            order.cancelDueToTimeout();
            orderRepo.save(order);
            log.info("eventId: {} orderId: {} evenType: {} action: ORDER_CANCELLED reason: STOCK_RESERVATION_EXPIRED status: SUCCESS",
                    stockReservationExpiredEvent.getEventId(),
                    stockReservationExpiredEvent.getOrderId(),
                    "stockReservationExpiredEvent"
            );
            processedEventRepo.save(new com.jagadesh.orderservice.model.ProcessedEvent(stockReservationExpiredEvent.getEventId()));
        }
    }

    @KafkaHandler
    @Transactional
    public void handleStockReservedEvent(StockReservedEvent stockReservedEvent) {
        if(processedEventRepo.existsById(stockReservedEvent.getEventId())) {
            return;
        }

        Order order = orderRepo.findById(stockReservedEvent.getOrderId()).get();
        order.stockReserved();
        order.paymentPending();

        orderRepo.save(order);
        orderEventPublisher.publishPaymentRequestedEvent(stockReservedEvent.getOrderId(), order.getTotalCost());
        processedEventRepo.save(new ProcessedEvent(stockReservedEvent.getEventId()));
        log.info("eventId: {} orderId: {} evenType: {} action: STOCK_RESERVED status: SUCCESS",
                stockReservedEvent.getEventId(),
                stockReservedEvent.getOrderId(),
                "stockReservedEvent"
        );
    }

    @KafkaHandler
    @Transactional
    public void handleStockReservationFailedEvent(StockReservationFailedEvent stockReservationFailedEvent) {
        if(processedEventRepo.existsById(stockReservationFailedEvent.getEventId())) {
            return;
        }

        Order order = orderRepo.findById(stockReservationFailedEvent.getOrderId()).get();
        order.cancelDueToStockFailure();

        orderRepo.save(order);
        processedEventRepo.save(new com.jagadesh.orderservice.model.ProcessedEvent(stockReservationFailedEvent.getEventId()));
        log.info("eventId: {} orderId: {} evenType: {} action: ORDER_CANCELLED reason: STOCK_RESERVATION_FAILED status: SUCCESS",
                stockReservationFailedEvent.getEventId(),
                stockReservationFailedEvent.getOrderId(),
                "stockReservationFailedEvent"
        );
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("Unknown Inventory event received by order service: {}", event);
    }
}
