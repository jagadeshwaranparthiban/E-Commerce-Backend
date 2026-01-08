package com.jagadesh.event.inventory;

import java.time.Instant;

public class ReleaseStockCommand {
    private final String eventId;
    private final String orderId;
    private final Instant occurredAt;

    public ReleaseStockCommand() {
        this.eventId = null;
        this.orderId = null;
        this.occurredAt = null;
    }

    public ReleaseStockCommand(String eventId, String orderId, Instant occurredAt) {
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
