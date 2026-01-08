package com.jagadesh.inventoryservice.dto;

import com.jagadesh.inventoryservice.model.InventoryStatus;

public record InventoryInfoDto(long productId, long availableQuantity, long reservedQuantity, InventoryStatus status) {
}
