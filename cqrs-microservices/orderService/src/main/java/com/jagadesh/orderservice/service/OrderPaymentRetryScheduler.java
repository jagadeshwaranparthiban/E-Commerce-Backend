package com.jagadesh.orderservice.service;

import com.jagadesh.orderservice.model.Order;
import com.jagadesh.orderservice.model.OrderStatus;
import com.jagadesh.orderservice.repository.OrderRepo;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@EnableScheduling
@Component
public class OrderPaymentRetryScheduler {
    private OrderRepo orderRepo;
    private OrderEventPublisher orderEventPublisher;

    public OrderPaymentRetryScheduler(OrderRepo orderRepo, OrderEventPublisher orderEventPublisher) {
        this.orderRepo = orderRepo;
        this.orderEventPublisher = orderEventPublisher;
    }


    //to be done
    @Scheduled(fixedRate = 30_000)
    public void retryFailedPayments() {
        List<Order> retryablePayments = orderRepo.findByOrderStatusAndNextPaymentAttemptAtBefore(OrderStatus.PAYMENT_FAILED, Instant.now());

        for(Order order : retryablePayments) {
            orderEventPublisher.publishPaymentRetryRequestedEvent(order.getOrderId());
        }
    }
}
