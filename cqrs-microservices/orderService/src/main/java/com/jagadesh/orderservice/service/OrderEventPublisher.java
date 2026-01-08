package com.jagadesh.orderservice.service;

import com.jagadesh.event.inventory.ReleaseStockCommand;
import com.jagadesh.event.inventory.StockReleasedEvent;
import com.jagadesh.event.order.OrderConfirmedEvent;
import com.jagadesh.event.order.OrderCreatedEvent;
import com.jagadesh.event.order.OrderItemPayload;
import com.jagadesh.event.payment.PaymentRequestedEvent;
import com.jagadesh.event.payment.PaymentRetryRequestedEvent;
import com.jagadesh.orderservice.dto.OrderItemInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderEventPublisher {
    private KafkaTemplate<String,Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private void logEventPublisher(String eventId, String orderId, String eventType) {
        log.info("eventId: {} orderId: {} eventType: {} action: PUBLISHED", eventId, orderId, eventType);
    }

    public void publishOrderCreatedEvent(String orderId, List<OrderItemInfoDto> items, BigDecimal totalCost) {
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                orderId,
                items.stream()
                        .map(item -> new OrderItemPayload(
                                item.productId(),
                                item.quantity()
                        ))
                        .toList(),
                totalCost,
                Instant.now()
        );
        kafkaTemplate.send(
                "order-event",
                orderId,
                orderCreatedEvent
        );
        logEventPublisher(orderCreatedEvent.getEventId(), orderId, "orderCreatedEvent");
    }

    public void publishReleaseStockCommand(String orderId) {
        ReleaseStockCommand releaseStockCommand = new ReleaseStockCommand(
                UUID.randomUUID().toString(),
                orderId,
                Instant.now()
        );
        kafkaTemplate.send(
                "order-event",
                orderId,
                releaseStockCommand
        );
        logEventPublisher(releaseStockCommand.getEventId(), orderId, "stockReleaseEvent");
    }

    public void publishPaymentRetryRequestedEvent(String orderId) {
        PaymentRetryRequestedEvent paymentRetryRequestedEvent = new PaymentRetryRequestedEvent(
                UUID.randomUUID().toString(),
                orderId,
                Instant.now()
        );
        kafkaTemplate.send(
                "order-event",
                orderId,
                paymentRetryRequestedEvent
        );
        logEventPublisher(paymentRetryRequestedEvent.getEventId(), orderId, "paymentRetryRequestedEvent");
    }

    public void publishPaymentRequestedEvent(String orderId, BigDecimal totalCost) {
        PaymentRequestedEvent paymentRequestedEvent = new PaymentRequestedEvent(
                UUID.randomUUID().toString(),
                orderId,
                totalCost,
                Instant.now()
        );
        kafkaTemplate.send(
                "order-event",
                orderId,
                paymentRequestedEvent
        );
        logEventPublisher(paymentRequestedEvent.getEventId(), orderId, "paymentRequestedEvent");
    }

    public void publishOrderConfirmedEvent(String orderId) {
        OrderConfirmedEvent orderConfirmedEvent = new OrderConfirmedEvent(
                UUID.randomUUID().toString(),
                orderId,
                Instant.now()
        );
        kafkaTemplate.send(
                "order-event",
                orderId,
                orderConfirmedEvent
        );
        logEventPublisher(orderConfirmedEvent.getEventId(), orderId, "orderConfirmedEvent");
    }
}
