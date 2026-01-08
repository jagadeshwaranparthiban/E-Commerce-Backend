package com.jagadesh.event.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderCreatedEvent {
    private final String eventId;
    private final String orderId;
    private final List<OrderItemPayload> itemInfo;
    //to be added: total cost
    private final BigDecimal totalCost;
    private final Instant occurredAt;

    public OrderCreatedEvent() {
        this.eventId = null;
        this.orderId = null;
        this.itemInfo = null;
        this.totalCost = null;
        this.occurredAt = null;
    }

    public OrderCreatedEvent(String eventId, String orderId, List<OrderItemPayload> itemInfo, BigDecimal totalCost, Instant occurredAt) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.itemInfo = itemInfo;
        this.totalCost = totalCost;
        this.occurredAt = occurredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderItemPayload> getItemInfo() {
        return itemInfo;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
