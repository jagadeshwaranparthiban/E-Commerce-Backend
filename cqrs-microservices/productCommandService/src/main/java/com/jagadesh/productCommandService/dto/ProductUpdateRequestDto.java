package com.jagadesh.productCommandService.dto;

import java.math.BigDecimal;

public record ProductUpdateRequestDto(String name, String description, BigDecimal price) {
}
