package com.jagadesh.paymentservice.service;

import com.jagadesh.event.payment.PaymentFailedEvent;
import com.jagadesh.event.payment.PaymentSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class PaymentEventPublisher {
    private KafkaTemplate<String,Object> kafkaTemplate;

    public PaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentSucceededEvent(String orderId) {
        PaymentSucceededEvent paymentSucceededEvent = new PaymentSucceededEvent(
                UUID.randomUUID().toString(),
                orderId,
                Instant.now()
        );
        kafkaTemplate.send(
                "payment-event",
                orderId,
                paymentSucceededEvent
        );
        log.info("eventId: {} orderId: {} evenType: {} action: PUBLISHED",
                paymentSucceededEvent.getEventId(),
                orderId,
                "paymentSucceededEvent"
        );
    }

    public void publishPaymentFailedEvent(String orderId) {
        PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                orderId,
                "Payment processing failed",
                Instant.now()
        );
        kafkaTemplate.send(
                "payment-event",
                orderId,
                paymentFailedEvent
        );
        log.info("eventId: {} orderId: {} evenType: {} action: PUBLISHED",
                paymentFailedEvent.getEventId(),
                orderId,
                "paymentFailedEvent"
        );
    }
}
