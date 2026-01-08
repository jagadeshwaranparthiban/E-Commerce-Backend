package com.jagadesh.event.product;

import java.time.Instant;

public class ProductNameChangedEvent {
    private final String eventId;
    private final long productId;
    private final String oldName;
    private final String newName;
    private final int version;
    private Instant occuredAt;

    public ProductNameChangedEvent() {
        this.eventId = null;
        this.productId = 0;
        this.oldName = null;
        this.newName = null;
        this.version = 0;
        this.occuredAt = null;
    }

    public ProductNameChangedEvent(
            String eventId, long productId, String oldName, String newName, int version, Instant occuredAt) {
        this.eventId = eventId;
        this.productId = productId;
        this.oldName = oldName;
        this.newName = newName;
        this.version = version;
        this.occuredAt = occuredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public long getProductId() {
        return productId;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    public int getVersion() {
        return version;
    }

    public Instant getOccuredAt() {
        return occuredAt;
    }
}
