package com.jagadesh.inventoryservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "stock_reservations")
public class StockReservation {
    @Id
    private String reservationId;
    private String orderId;
    private long productId;
    private int quantity;
    private Instant createdAt;
    private Instant expiresAt;

    public StockReservation() {}

    public StockReservation(String reservationId, String orderId, long productId, int quantity, Instant createdAt, Instant expiresAt) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
