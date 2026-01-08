package com.jagadesh.event.inventory;

import java.math.BigDecimal;
import java.time.Instant;

public class StockReservedEvent {
    private final String eventId;
    private final String orderId;
    //to be added: total cost
    private final BigDecimal totalCost;
    private final Instant occuredAt;

    public StockReservedEvent() {
        this.eventId = null;
        this.orderId = null;
        this.totalCost = null;
        this.occuredAt = null;
    }

    public StockReservedEvent(String eventId, String orderId, BigDecimal totalCost, Instant occuredAt) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.totalCost = totalCost;
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

    public BigDecimal getTotalCost() {
        return totalCost;
    }
}
