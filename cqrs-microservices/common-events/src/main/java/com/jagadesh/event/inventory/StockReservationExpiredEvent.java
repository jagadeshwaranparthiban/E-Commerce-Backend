package com.jagadesh.event.inventory;

import java.time.Instant;

public class StockReservationExpiredEvent {

    private final String eventId;
    private final String orderId;
    private final Instant occurredAt;

    public StockReservationExpiredEvent() {
        this.eventId = null;
        this.orderId = null;
        this.occurredAt = null;
    }

    public StockReservationExpiredEvent(
            String eventId,
            String orderId,
            Instant occurredAt
    ) {
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

