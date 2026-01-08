package com.jagadesh.event.product;

import java.time.Instant;

public class ProductDiscontinuedEvent {
    private final String eventId;
    private final long productId;
    private final int version;
    private Instant occuredAt;

    public ProductDiscontinuedEvent() {
        this.eventId = null;
        this.productId = 0;
        this.version = 0;
        this.occuredAt = null;
    }

    public ProductDiscontinuedEvent(
            String eventId, long productId, int version, Instant occuredAt) {
        this.eventId = eventId;
        this.productId = productId;
        this.version = version;
        this.occuredAt = occuredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public long getProductId() {
        return productId;
    }

    public int getVersion() {
        return version;
    }

    public Instant getOccuredAt() {
        return occuredAt;
    }
}
