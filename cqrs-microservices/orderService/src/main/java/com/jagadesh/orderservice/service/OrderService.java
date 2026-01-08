package com.jagadesh.orderservice.service;

import com.jagadesh.orderservice.dto.OrderCreateRequestDto;
import com.jagadesh.orderservice.dto.OrderItemInfoDto;
import com.jagadesh.orderservice.dto.OrderStatusResponseDto;
import com.jagadesh.orderservice.model.Order;
import com.jagadesh.orderservice.model.OrderItem;
import com.jagadesh.orderservice.model.OrderStatus;
import com.jagadesh.orderservice.repository.OrderItemRepo;
import com.jagadesh.orderservice.repository.OrderRepo;
import com.jagadesh.orderservice.repository.ProductPriceViewRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;
    private OrderEventPublisher orderEventPublisher;
    private ProductPriceViewRepo productPriceViewRepo;

    public OrderService(
            OrderRepo orderRepo, OrderItemRepo orderItemRepo, OrderEventPublisher orderEventPublisher, ProductPriceViewRepo productPriceViewRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.orderEventPublisher = orderEventPublisher;
        this.productPriceViewRepo = productPriceViewRepo;
    }

    public OrderStatusResponseDto createOrder(OrderCreateRequestDto orderCreateRequestDto) {
        String orderId = "ORD-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
        BigDecimal totalCost = calculateTotalCost(orderCreateRequestDto.itemInfo());
        Order order = new Order(orderId, totalCost);
        orderRepo.save(order);

        orderCreateRequestDto.itemInfo().stream()
                .map(item -> new OrderItem(
                        orderId,
                        item.productId(),
                        item.quantity()
                ))
                .forEach(orderItemRepo::save);

        orderEventPublisher.publishOrderCreatedEvent(orderId, orderCreateRequestDto.itemInfo(), totalCost);
        return new OrderStatusResponseDto(orderId, order.getOrderStatus().toString());
    }

    private BigDecimal calculateTotalCost(List<OrderItemInfoDto> itemInfo) {
        BigDecimal totalCost = BigDecimal.ZERO;
        for(OrderItemInfoDto item: itemInfo){
            BigDecimal price = productPriceViewRepo.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"))
                    .getPrice();
            BigDecimal itemCost = price.multiply(BigDecimal.valueOf(item.quantity()));
            totalCost = totalCost.add(itemCost);
        }
        return totalCost;
    }

    public OrderStatusResponseDto getOrderStatus(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderStatusResponseDto(orderId, order.getOrderStatus().toString());
    }

    public void confirmOrder(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.confirm();
        orderRepo.save(order);
    }

    public void rejectOrder(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if(order.isFinal()) return;

        order.onPaymentFailed(Instant.now());
        orderRepo.save(order);

        if(order.getOrderStatus() == OrderStatus.CANCELLED_TIMEOUT) {
            orderEventPublisher.publishReleaseStockCommand(orderId);
        }
    }
}
