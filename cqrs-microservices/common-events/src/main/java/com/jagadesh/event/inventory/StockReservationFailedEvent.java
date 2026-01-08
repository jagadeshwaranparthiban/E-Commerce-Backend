package com.jagadesh.event.inventory;

import java.time.Instant;

public class StockReservationFailedEvent {

    private final String eventId;
    private final String orderId;
    private final Long productId;
    private final int requestedQuantity;
    private final String reason;
    private final Instant occurredAt;

    public StockReservationFailedEvent() {
        this.eventId = null;
        this.orderId = null;
        this.productId = null;
        this.requestedQuantity = 0;
        this.reason = null;
        this.occurredAt = null;
    }

    public StockReservationFailedEvent(
            String eventId,
            String orderId,
            Long productId,
            int requestedQuantity,
            String reason,
            Instant occurredAt
    ) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.reason = reason;
        this.occurredAt = occurredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public String getReason() {
        return reason;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}

