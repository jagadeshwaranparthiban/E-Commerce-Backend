package com.jagadesh.event.payment;

import java.time.Instant;

public class PaymentFailedEvent {
    private final String eventId;
    private final String orderId;
    private final String reason;
    private final Instant occurredAt;

    public PaymentFailedEvent() {
        this.eventId = null;
        this.orderId = null;
        this.reason = null;
        this.occurredAt = null;
    }

    public PaymentFailedEvent(String eventId, String orderId, String reason, Instant occurredAt) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.reason = reason;
        this.occurredAt = occurredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
