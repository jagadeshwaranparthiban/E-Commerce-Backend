package com.jagadesh.orderservice.dto;

import java.util.List;

public record OrderCreateRequestDto(List<OrderItemInfoDto> itemInfo) {
}
