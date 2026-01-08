package com.jagadesh.orderservice.handler;

import com.jagadesh.event.payment.PaymentRetryRequestedEvent;
import com.jagadesh.orderservice.model.Order;
import com.jagadesh.orderservice.model.OrderStatus;
import com.jagadesh.orderservice.repository.OrderRepo;
import com.jagadesh.orderservice.repository.ProcessedEventRepo;
import com.jagadesh.orderservice.service.OrderEventPublisher;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@KafkaListener(
        topics = "order-event",
        groupId = "order-group",
        containerFactory = "kafkaListenerContainerFactory"
)
@Service
@Slf4j
public class OrderEventHandler {
    private OrderEventPublisher orderEventPublisher;
    private ProcessedEventRepo processedEventRepo;
    private OrderRepo orderRepo;

    public OrderEventHandler(
            OrderEventPublisher orderEventPublisher, ProcessedEventRepo processedEventRepo, OrderRepo orderRepo) {
        this.orderEventPublisher = orderEventPublisher;
        this.processedEventRepo = processedEventRepo;
        this.orderRepo = orderRepo;
    }

    @KafkaHandler
    @Transactional
    public void handlePaymentRetryRequestedEvent(PaymentRetryRequestedEvent paymentRetryRequestedEvent) {
        if(processedEventRepo.existsById(paymentRetryRequestedEvent.getEventId())) {
            return;
        }

        Order retryableOrder = orderRepo.findById(paymentRetryRequestedEvent.getOrderId()).get();

        if(retryableOrder.getOrderStatus() != OrderStatus.PAYMENT_FAILED) {
            log.info("eventId: {} orderId: {} evenType: {} action: PAYMENT_RETRY_IGNORED reason: ORDER_NOT_IN_PAYMENT_FAILED_STATUS status: SUCCESS",
                    paymentRetryRequestedEvent.getEventId(),
                    paymentRetryRequestedEvent.getOrderId(),
                    "PaymentRetryRequestedEvent"
            );
            processedEventRepo.save(new com.jagadesh.orderservice.model.ProcessedEvent(paymentRetryRequestedEvent.getEventId()));
            return;
        }

        retryableOrder.retryPayment();
        orderRepo.save(retryableOrder);

        orderEventPublisher.publishPaymentRequestedEvent(retryableOrder.getOrderId(), retryableOrder.getTotalCost());
        processedEventRepo.save(new com.jagadesh.orderservice.model.ProcessedEvent(paymentRetryRequestedEvent.getEventId()));
        log.info("eventId: {} orderId: {} evenType: {} action: PAYMENT_RETRY_SCHEDULED status: SUCCESS",
                paymentRetryRequestedEvent.getEventId(),
                paymentRetryRequestedEvent.getOrderId(),
                "PaymentRetryRequestedEvent"
        );
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("Unknown order event received by order service: {}", event);
    }
}
