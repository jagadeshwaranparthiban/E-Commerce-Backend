package com.jagadesh.orderservice.controller;

import com.jagadesh.orderservice.dto.OrderCreateRequestDto;
import com.jagadesh.orderservice.dto.OrderStatusResponseDto;
import com.jagadesh.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderStatusResponseDto> createOrder(@RequestBody OrderCreateRequestDto orderCreateRequestDto) {
        return ResponseEntity.accepted().body(orderService.createOrder(orderCreateRequestDto));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<OrderStatusResponseDto> getOrderStatus(@PathVariable("id") String id) {
        return ResponseEntity.ok().body(orderService.getOrderStatus(id));
    }
}
