package com.jagadesh.orderservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private String orderId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private BigDecimal totalCost;
    private Instant createdAt;
    private int paymentAttempts;
    private Instant nextPaymentAttemptAt;

    public Order() {}

    public Order(String orderId, BigDecimal totalCost) {
        this.orderId = orderId;
        this.orderStatus = OrderStatus.CREATED;
        this.totalCost = totalCost;
        this.createdAt = Instant.now();
        this.paymentAttempts = 0;
        this.nextPaymentAttemptAt = null;
    }

    public Order(String orderId, OrderStatus orderStatus, BigDecimal totalCost, Instant createdAt) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.totalCost = totalCost;
        this.paymentAttempts = 0;
        this.nextPaymentAttemptAt = null;
    }

    public void confirm() {
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void cancelDueToStockFailure() {
        this.orderStatus = OrderStatus.CANCELLED_STOCK_FAILED;
    }

    public void cancelDueToTimeout() {
        this.orderStatus = OrderStatus.CANCELLED_TIMEOUT;
    }

    public void stockReserved() {
        requiredStatus(OrderStatus.CREATED);
        this.orderStatus = OrderStatus.STOCK_RESERVED;
    }

    public void paymentPending() {
        requiredStatus(OrderStatus.STOCK_RESERVED);
        this.orderStatus = OrderStatus.PAYMENT_PENDING;
    }

    public void paymentFailed() {
        requiredStatus(OrderStatus.PAYMENT_PENDING);
        this.orderStatus = OrderStatus.PAYMENT_FAILED;
    }

    public void stockReservationFailed() {
        requiredStatus(OrderStatus.CREATED);
        this.orderStatus = OrderStatus.CANCELLED_STOCK_FAILED;
    }

    public void ship() {
        requiredStatus(OrderStatus.CONFIRMED);
        this.orderStatus = OrderStatus.SHIPPED;
    }

    public void deliver() {
        requiredStatus(OrderStatus.SHIPPED);
        this.orderStatus = OrderStatus.DELIVERED;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getPaymentAttempts() {
        return paymentAttempts;
    }

    public Instant getNextPaymentAttemptAt() {
        return nextPaymentAttemptAt;
    }

    public void incrementPaymentAttempts() {
        this.paymentAttempts++;
    }

    private void requiredStatus(OrderStatus status) {
        if (this.orderStatus != status) {
            throw new IllegalStateException("Order " + orderId + " is in status " + orderStatus + ", required: " + status);
        }
    }

    public void retryPayment() {
        requiredStatus(OrderStatus.PAYMENT_FAILED);
        this.orderStatus = OrderStatus.PAYMENT_PENDING;
    }

    public boolean isFinal() {
        return orderStatus == OrderStatus.CONFIRMED
                || orderStatus == OrderStatus.CANCELLED_TIMEOUT
                || orderStatus == OrderStatus.CANCELLED_STOCK_FAILED
                || orderStatus == OrderStatus.DELIVERED;
    }

    public void onPaymentFailed(Instant now) {
        requiredStatus(OrderStatus.PAYMENT_PENDING);

        this.paymentAttempts++;
        if(paymentAttempts > 3) {
            this.orderStatus = OrderStatus.CANCELLED_TIMEOUT;
            return;
        }

        this.orderStatus = OrderStatus.PAYMENT_FAILED;

        Duration backoff = switch(paymentAttempts) {
            case 1 -> Duration.ofSeconds(30);
            case 2 -> Duration.ofMinutes(2);
            case 3 -> Duration.ofMinutes(5);
            default -> throw new IllegalStateException("unexpected value: " + paymentAttempts);
        };

        this.nextPaymentAttemptAt = now.plus(backoff);
    }
}
