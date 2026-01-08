package com.jagadesh.paymentservice.handler;

import com.jagadesh.event.payment.PaymentRequestedEvent;
import com.jagadesh.event.payment.PaymentRetryRequestedEvent;
import com.jagadesh.paymentservice.model.Payment;
import com.jagadesh.paymentservice.model.ProcessedEvent;
import com.jagadesh.paymentservice.repository.PaymentRepo;
import com.jagadesh.paymentservice.repository.ProcessedEventRepo;
import com.jagadesh.paymentservice.service.PaymentEventPublisher;
import com.jagadesh.paymentservice.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@KafkaListener(
        topics = "order-event",
        groupId = "payment-group"
)
@Service
@Slf4j
public class PaymentOrderEventHandler {
    private PaymentRepo paymentRepo;
    private ProcessedEventRepo processedEventRepo;
    private PaymentService paymentService;
    private PaymentEventPublisher paymentEventPublisher;

    public PaymentOrderEventHandler(
            PaymentRepo paymentRepo, ProcessedEventRepo processedEventRepo, PaymentService paymentService, PaymentEventPublisher paymentEventPublisher) {
        this.paymentRepo = paymentRepo;
        this.processedEventRepo = processedEventRepo;
        this.paymentService = paymentService;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @KafkaHandler
    @Transactional
    public void handlePaymentRequestedEvent(PaymentRequestedEvent paymentRequestedEvent) {
        if(processedEventRepo.existsById(paymentRequestedEvent.getEventId())) {
            return;
        }

        log.info("eventId: {} orderId: {} evenType: {} action: RECEIVED",
                paymentRequestedEvent.getEventId(),
                paymentRequestedEvent.getOrderId(),
                "paymentRequestedEvent"
        );

        Payment newPayment = paymentService.initiatePayment(paymentRequestedEvent.getOrderId());
        boolean paymentSuccess = paymentService.simulatePayment(paymentRequestedEvent.getOrderId());

        if(paymentSuccess) {
            newPayment.markAsSucceeded();
            paymentEventPublisher.publishPaymentSucceededEvent(paymentRequestedEvent.getOrderId());
        }else {
            newPayment.markAsFailed();
            paymentEventPublisher.publishPaymentFailedEvent(paymentRequestedEvent.getOrderId());
        }
        paymentRepo.save(newPayment);
        processedEventRepo.save(new ProcessedEvent(paymentRequestedEvent.getEventId()));
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("Unhandled order event received: {}", event.getClass().getName());
    }
}
