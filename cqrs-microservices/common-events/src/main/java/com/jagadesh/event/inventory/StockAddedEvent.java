package com.jagadesh.event.inventory;

import java.time.Instant;

public class StockAddedEvent {
    private final String eventId;
    private final long productId;
    private final long quantity;
    private final Instant occuredAt;

    public StockAddedEvent() {
        eventId = null;
        productId = 0;
        quantity = 0;
        occuredAt = null;
    }

    public StockAddedEvent(
            String eventId,
            long productId,
            long quantity,
            Instant occuredAt
    ) {
        this.eventId = eventId;
        this.productId = productId;
        this.quantity = quantity;
        this.occuredAt = occuredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public long getProductId() {
        return productId;
    }

    public long getQuantity() {
        return quantity;
    }

    public Instant getOccuredAt() {
        return occuredAt;
    }
}
