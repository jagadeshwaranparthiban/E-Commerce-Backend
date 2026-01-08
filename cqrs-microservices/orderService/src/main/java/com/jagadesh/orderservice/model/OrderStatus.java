package com.jagadesh.orderservice.model;

public enum OrderStatus {
    CREATED,
    STOCK_RESERVED,
    PAYMENT_PENDING,
    PAYMENT_FAILED,
    CONFIRMED,
    CANCELLED_TIMEOUT,
    CANCELLED_STOCK_FAILED,
    SHIPPED,
    DELIVERED
}
