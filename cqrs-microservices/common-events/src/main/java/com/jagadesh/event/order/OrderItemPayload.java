package com.jagadesh.event.order;

public class OrderItemPayload {

    private final Long productId;
    private final int quantity;

    public OrderItemPayload() {
        this.productId = null;
        this.quantity = 0;
    }

    public OrderItemPayload(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}

