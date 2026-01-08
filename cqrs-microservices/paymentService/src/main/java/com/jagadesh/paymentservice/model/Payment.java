package com.jagadesh.paymentservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String orderId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public Payment() {}

    public Payment(String orderId, PaymentStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public void markAsSucceeded() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }
}
