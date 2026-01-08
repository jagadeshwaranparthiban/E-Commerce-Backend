package com.jagadesh.inventoryservice.controller;

import com.jagadesh.inventoryservice.dto.AddStockRequestDto;
import com.jagadesh.inventoryservice.model.Inventory;
import com.jagadesh.inventoryservice.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<Inventory> addStock(@PathVariable("productId") long productId, @RequestBody AddStockRequestDto stockInfo) {
        return ResponseEntity.accepted().body(inventoryService.addStock(productId, stockInfo));
    }
}
