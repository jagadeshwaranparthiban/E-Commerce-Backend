package com.jagadesh.event.payment;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentRequestedEvent {
    private final String eventId;
    private final String orderId;
    private final BigDecimal totalCost;
    private final Instant occurredAt;

    public PaymentRequestedEvent() {
        this.eventId = null;
        this.orderId = null;
        this.totalCost = null;
        this.occurredAt = null;
    }

    public PaymentRequestedEvent(String eventId, String orderId, BigDecimal totalCost, Instant occurredAt) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.totalCost = totalCost;
        this.occurredAt = occurredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
