package com.jagadesh.event.payment;

import java.time.Instant;

public class PaymentSucceededEvent {
    private final String eventId;
    private final String orderId;
    private final Instant occurredAt;

    public PaymentSucceededEvent() {
        this.eventId = null;
        this.orderId = null;
        this.occurredAt = null;
    }

    public PaymentSucceededEvent(String eventId, String orderId, Instant occurredAt) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.occurredAt = occurredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
