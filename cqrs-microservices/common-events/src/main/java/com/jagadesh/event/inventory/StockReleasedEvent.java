package com.jagadesh.event.inventory;

import java.time.Instant;

public class StockReleasedEvent {
    private final String eventId;
    private final String orderId;
    private final Instant occuredAt;

    public StockReleasedEvent() {
        this.eventId = null;
        this.orderId = null;
        this.occuredAt = null;
    }

    public StockReleasedEvent(String eventId, String orderId, Instant occuredAt) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.occuredAt = occuredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Instant getOccuredAt() {
        return occuredAt;
    }
}
