package com.jagadesh.paymentservice.service;

import com.jagadesh.paymentservice.model.Payment;
import com.jagadesh.paymentservice.model.PaymentStatus;
import com.jagadesh.paymentservice.repository.PaymentRepo;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private PaymentRepo paymentRepo;

    public PaymentService(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    //simulating payment
    public boolean simulatePayment(String orderId) {
        return Math.random() > 0.4; // 60% chance of success
    }

    public Payment initiatePayment(String orderId) {
        Payment payment = new Payment(
                orderId,
                PaymentStatus.INITIATED
        );
        return paymentRepo.save(payment);
    }
}
