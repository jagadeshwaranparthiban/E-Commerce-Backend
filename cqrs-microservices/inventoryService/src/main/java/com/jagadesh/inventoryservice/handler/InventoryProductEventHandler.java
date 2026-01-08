package com.jagadesh.inventoryservice.handler;

import com.jagadesh.event.product.ProductCreatedEvent;
import com.jagadesh.event.product.ProductDiscontinuedEvent;
import com.jagadesh.inventoryservice.exception.NonRetryableEventException;
import com.jagadesh.inventoryservice.model.Inventory;
import com.jagadesh.inventoryservice.model.ProcessedEvent;
import com.jagadesh.inventoryservice.repository.InventoryRepo;
import com.jagadesh.inventoryservice.repository.ProcessedEventRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@KafkaListener(
        topics = "product-event",
        groupId = "inventory-group"
)
@Service
@Slf4j
public class InventoryProductEventHandler {

    private InventoryRepo inventoryRepo;
    private ProcessedEventRepo processedEventRepo;

    public InventoryProductEventHandler(InventoryRepo inventoryRepo, ProcessedEventRepo processedEventRepo) {
        this.inventoryRepo = inventoryRepo;
        this.processedEventRepo = processedEventRepo;
    }

    @KafkaHandler
    @Transactional
    public void processInventoryInitiationEvent(ProductCreatedEvent productCreatedEvent) {
        if(processedEventRepo.existsById(productCreatedEvent.getEventId())) {
            return;
        }

        Inventory newInventory = new Inventory(
                productCreatedEvent.getProductId()
        );

        inventoryRepo.save(newInventory);
        processedEventRepo.save(new ProcessedEvent(productCreatedEvent.getEventId()));
        log.info("eventId: {} productId: {} eventType: {} action: INIT_INVENTORY status: SUCCESS",
                productCreatedEvent.getEventId(),
                productCreatedEvent.getProductId(),
                "productCreatedEvent"
        );
    }

    @KafkaHandler
    @Transactional
    public void processInventoryDiscontinuedEvent(ProductDiscontinuedEvent productDiscontinuedEvent) {
        if(processedEventRepo.existsById(productDiscontinuedEvent.getEventId())) {
            return;
        }

        Inventory inventory = inventoryRepo.findById(productDiscontinuedEvent.getProductId())
                .orElseThrow(()->new NonRetryableEventException(
                "Inventory not found for productId="+productDiscontinuedEvent.getProductId()
        ));

        inventory.discontinue();
        inventoryRepo.save(inventory);
        processedEventRepo.save(new ProcessedEvent(productDiscontinuedEvent.getEventId()));

        log.info("eventId: {} productId: {} eventType: {} action: DISCONTINUE_INVENTORY status: SUCCESS",
                productDiscontinuedEvent.getEventId(),
                productDiscontinuedEvent.getProductId(),
                "productDiscontinuedEvent"
        );
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("Unknown product event received by inventory service: {}", event);
    }
}
