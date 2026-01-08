package com.jagadesh.event.product;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductCreatedEvent {
    private final String eventId;
    private final long productId;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final int version;
    private Instant occuredAt;

    public ProductCreatedEvent() {
        this.eventId = null;
        this.productId = 0;
        this.name = null;
        this.description = null;
        this.price = null;
        this.version = 0;
        this.occuredAt = null;
    }

    public ProductCreatedEvent(
            String eventId, long productId, String name, String description, BigDecimal price, int version,  Instant occuredAt) {
        this.eventId = eventId;
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.version = version;
        this.occuredAt = occuredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getVersion() {
        return version;
    }

    public  Instant getOccuredAt() {
        return occuredAt;
    }
}
