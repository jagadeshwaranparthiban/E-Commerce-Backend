package com.jagadesh.orderservice.repository;

import com.jagadesh.orderservice.model.Order;
import com.jagadesh.orderservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, String> {
    List<Order> findByOrderStatus(String orderStatus);

    List<Order> findByOrderStatusAndNextPaymentAttemptAtBefore(OrderStatus orderStatus, Instant now);
}
