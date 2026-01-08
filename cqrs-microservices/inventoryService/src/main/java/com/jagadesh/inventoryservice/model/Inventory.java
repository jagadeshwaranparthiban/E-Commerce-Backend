package com.jagadesh.inventoryservice.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "inventory")
public class  Inventory {
    @Id
    private long productId;
    private long availableQuantity;
    private long reservedQuantity;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    protected Inventory() {}

    public Inventory(long productId) {
        this.productId = productId;
        this.availableQuantity = 0;
        this.reservedQuantity = 0;
        this.status = InventoryStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void discontinue() {
        this.status = InventoryStatus.DISCONTINUED;
        this.updatedAt = Instant.now();
    }

    public void increaseAvailableQuantity(long qty) {
        this.availableQuantity += qty;
        this.updatedAt = Instant.now();
    }

    public void decreaseAvailableQuantity(long qty) {
        this.availableQuantity -= qty;
        this.updatedAt = Instant.now();
    }

    public void decreaseReservedQuantity(int quantity) {
        if(reservedQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient reserved quantity to decrease");
        }
        this.reservedQuantity -= quantity;
        this.updatedAt = Instant.now();
    }

    public void reserveStock(long qty) {
        this.reservedQuantity += qty;
    }

    public long getProductId() {
        return productId;
    }

    public long getAvailableQuantity() {
        return availableQuantity;
    }

    public long getReservedQuantity() {
        return reservedQuantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public InventoryStatus getStatus() {
        return this.status;
    }

}
