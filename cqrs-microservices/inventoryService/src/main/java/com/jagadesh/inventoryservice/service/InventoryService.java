package com.jagadesh.inventoryservice.service;

import com.jagadesh.event.inventory.StockAddedEvent;
import com.jagadesh.inventoryservice.dto.AddStockRequestDto;
import com.jagadesh.inventoryservice.dto.StockAvailabilityStatus;
import com.jagadesh.inventoryservice.exception.CustomException;
import com.jagadesh.inventoryservice.exception.ReservationNotFoundException;
import com.jagadesh.inventoryservice.model.Inventory;
import com.jagadesh.inventoryservice.model.InventoryStatus;
import com.jagadesh.inventoryservice.model.StockReservation;
import com.jagadesh.inventoryservice.repository.InventoryRepo;
import com.jagadesh.inventoryservice.repository.StockReservationRepo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryService {
    private final Duration EXPIRATION_DURATION = Duration.ofMinutes(15);
    private InventoryRepo inventoryRepo;
    private InventoryEventPublisher inventoryEventPublisher;
    private StockReservationRepo stockReservationRepo;

    public InventoryService(
            InventoryRepo inventoryRepo, InventoryEventPublisher inventoryEventPublisher, StockReservationRepo stockReservationRepo) {
        this.inventoryRepo = inventoryRepo;
        this.inventoryEventPublisher = inventoryEventPublisher;
        this.stockReservationRepo = stockReservationRepo;
    }

    public Inventory addStock(long productId, AddStockRequestDto stockInfo) {
        if(stockInfo.quantity() <= 0) {
            throw new CustomException("Quantity should be greater than 0");
        }

        Optional<Inventory> optional = inventoryRepo.findById(productId);
        if(optional.isEmpty()) {
            throw new CustomException("Product not found.");
        }

        Inventory inventory = optional.get();
        if(inventory.getStatus() == InventoryStatus.DISCONTINUED) {
            throw new CustomException("Inventory status of this product is DISCONTINUED.");
        }

        inventoryEventPublisher.publishStockAddedEvent(
                new StockAddedEvent(
                        UUID.randomUUID().toString(),
                        productId,
                        stockInfo.quantity(),
                        Instant.now()
                )
        );

        return inventory;
    }

    public StockAvailabilityStatus checkStockAvailability(long productId, long quantity) {
        if(quantity <= 0) {
            return StockAvailabilityStatus.FAILED;
            //throw new CustomException("Quantity should be greater than 0");
        }

        Optional<Inventory> optional = inventoryRepo.findById(productId);
        if(optional.isEmpty()) {
            return StockAvailabilityStatus.FAILED;
        }

        Inventory inventory = optional.get();
        if(inventory.getStatus() == InventoryStatus.DISCONTINUED
                || inventory.getAvailableQuantity() < quantity
        ) {
            return StockAvailabilityStatus.FAILED;
        }
        return StockAvailabilityStatus.SUCCESS;
    }

    public void reserveStock(String orderId, long productId, int quantity) {
        Inventory inventory = inventoryRepo.findById(productId).get();

        inventory.decreaseAvailableQuantity(quantity);
        inventory.reserveStock(quantity);

        Instant reservedTime = Instant.now();
        Instant expirationTime = Instant.now().plus(EXPIRATION_DURATION);

        StockReservation newReservation = new StockReservation(
                UUID.randomUUID().toString(),
                orderId,
                productId,
                quantity,
                reservedTime,
                expirationTime
        );
        stockReservationRepo.save(newReservation);
        inventoryRepo.save(inventory);
    }

    public void releaseStock(String orderId) {
        List<StockReservation> reservation = stockReservationRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ReservationNotFoundException("Stock reservation not found for orderId: " + orderId));

        for(StockReservation res: reservation) {
            Inventory inventory = inventoryRepo.findById(res.getProductId()).get();
            inventory.increaseAvailableQuantity(res.getQuantity());
            inventory.decreaseReservedQuantity(res.getQuantity());
            stockReservationRepo.delete(res);
            inventoryRepo.save(inventory);
        }
    }

    public void consumeReservedStocks(String orderId) {
        List<StockReservation> reservedItems = stockReservationRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ReservationNotFoundException("Stock reservation not found for orderId: " + orderId));
        for(StockReservation res: reservedItems) {
            Inventory inventory = inventoryRepo.findById(res.getProductId()).get();
            inventory.decreaseReservedQuantity(res.getQuantity());
            stockReservationRepo.delete(res);
            inventoryRepo.save(inventory);
        }
    }
}
