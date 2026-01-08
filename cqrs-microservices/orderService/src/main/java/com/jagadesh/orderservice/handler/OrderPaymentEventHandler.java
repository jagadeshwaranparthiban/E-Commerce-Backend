package com.jagadesh.orderservice.handler;

import com.jagadesh.event.payment.PaymentFailedEvent;
import com.jagadesh.event.payment.PaymentSucceededEvent;
import com.jagadesh.orderservice.model.ProcessedEvent;
import com.jagadesh.orderservice.repository.ProcessedEventRepo;
import com.jagadesh.orderservice.service.OrderEventPublisher;
import com.jagadesh.orderservice.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@KafkaListener(
        topics = "payment-event",
        groupId = "order-group"
)
@Service
@Slf4j
public class OrderPaymentEventHandler {

    private OrderService orderService;
    private OrderEventPublisher orderEventPublisher;
    private ProcessedEventRepo processedEventRepo;

    public OrderPaymentEventHandler(OrderService orderService, OrderEventPublisher orderEventPublisher, ProcessedEventRepo processedEventRepo) {
        this.orderService = orderService;
        this.orderEventPublisher = orderEventPublisher;
        this.processedEventRepo = processedEventRepo;
    }

    @KafkaHandler
    @Transactional
    public void handlePaymentSucceededEvent(PaymentSucceededEvent paymentSucceededEvent) {
        if(processedEventRepo.existsById(paymentSucceededEvent.getOrderId())) {
            return;
        }

        orderService.confirmOrder(paymentSucceededEvent.getOrderId());
        orderEventPublisher.publishOrderConfirmedEvent(paymentSucceededEvent.getOrderId());

        processedEventRepo.save(new ProcessedEvent(paymentSucceededEvent.getEventId()));

        log.info("eventId: {} orderId: {} eventType: {} action: ORDER_CONFIRMED status: SUCCESS",
                paymentSucceededEvent.getEventId(),
                paymentSucceededEvent.getOrderId(),
                "paymentSucceededEvent"
        );
    }

    @KafkaHandler
    @Transactional
    public void handlePaymentFailedEvent(PaymentFailedEvent paymentFailedEvent) {
        if(processedEventRepo.existsById(paymentFailedEvent.getOrderId())) {
            return;
        }

        orderService.rejectOrder(paymentFailedEvent.getOrderId());
        processedEventRepo.save(new ProcessedEvent(paymentFailedEvent.getEventId()));

        orderEventPublisher.publishReleaseStockCommand(paymentFailedEvent.getOrderId());
        log.info("eventId: {} orderId: {} eventType: {} action: ORDER_CANCELLED reason: PAYMENT_FAILED status: SUCCESS",
                paymentFailedEvent.getEventId(),
                paymentFailedEvent.getOrderId(),
                "paymentFailedEvent"
        );
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("Unhandled product event received: {}", event.getClass().getName());
    }
}
