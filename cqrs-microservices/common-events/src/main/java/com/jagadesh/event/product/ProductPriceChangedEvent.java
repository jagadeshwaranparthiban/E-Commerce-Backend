package com.jagadesh.event.product;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductPriceChangedEvent {
    private final String eventId;
    private final long productId;
    private final BigDecimal oldPrice;
    private final BigDecimal newPrice;
    private final int version;
    private Instant occuredAt;

    public ProductPriceChangedEvent() {
        this.eventId = null;
        this.productId = 0;
        this.oldPrice = null;
        this.newPrice = null;
        this.version = 0;
        this.occuredAt = null;
    }

    public ProductPriceChangedEvent(
            String eventId, long productId, BigDecimal oldPrice, BigDecimal newPrice, int version,  Instant occuredAt) {
        this.eventId = eventId;
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.version = version;
        this.occuredAt = occuredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public long getProductId() {
        return productId;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public int getVersion() {
        return version;
    }

    public Instant getOccuredAt() {
        return occuredAt;
    }
}
