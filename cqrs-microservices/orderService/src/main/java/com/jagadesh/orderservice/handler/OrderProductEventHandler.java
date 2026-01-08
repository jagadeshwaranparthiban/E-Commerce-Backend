package com.jagadesh.orderservice.handler;

import com.jagadesh.event.product.ProductCreatedEvent;
import com.jagadesh.event.product.ProductDiscontinuedEvent;
import com.jagadesh.event.product.ProductPriceChangedEvent;
import com.jagadesh.orderservice.model.ProcessedEvent;
import com.jagadesh.orderservice.model.ProductPriceView;
import com.jagadesh.orderservice.repository.ProcessedEventRepo;
import com.jagadesh.orderservice.repository.ProductPriceViewRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@KafkaListener(
        topics = "product-event",
        groupId = "order-group",
        containerFactory = "kafkaListenerContainerFactory"
)
@Service
@Slf4j
public class OrderProductEventHandler {

    private ProductPriceViewRepo productPriceViewRepo;
    private ProcessedEventRepo processedEventRepo;

    public OrderProductEventHandler(ProductPriceViewRepo productPriceViewRepo, ProcessedEventRepo processedEventRepo) {
        this.productPriceViewRepo = productPriceViewRepo;
        this.processedEventRepo = processedEventRepo;
    }

    private void logEventPublisher(String eventId, long productId, String eventType, String action) {
        log.info("eventId: {} productId: {} evenType: {} action: {} status: SUCCESS",
                eventId,
                productId,
                eventType,
                action);
    }

    @KafkaHandler
    @Transactional
    public void handleProductCreatedEvent(ProductCreatedEvent productCreatedEvent){
        if(processedEventRepo.existsById(productCreatedEvent.getEventId())) {
            return;
        }

        ProductPriceView newProduct = new ProductPriceView(
                productCreatedEvent.getProductId(),
                productCreatedEvent.getPrice()
        );
        productPriceViewRepo.save(newProduct);

        processedEventRepo.save(new ProcessedEvent(productCreatedEvent.getEventId()));
        logEventPublisher(
                productCreatedEvent.getEventId(),
                productCreatedEvent.getProductId(),
                "ProductCreatedEvent",
                "SAVED to ProductPriceView"
        );

    }

    @KafkaHandler
    @Transactional
    public void handleProductDiscontinuedEvent(ProductDiscontinuedEvent productDiscontinuedEvent) {
        if(processedEventRepo.existsById(productDiscontinuedEvent.getEventId())) {
            return;
        }

        productPriceViewRepo.deleteById(productDiscontinuedEvent.getProductId());

        processedEventRepo.save(new ProcessedEvent(productDiscontinuedEvent.getEventId()));
        logEventPublisher(
                productDiscontinuedEvent.getEventId(),
                productDiscontinuedEvent.getProductId(),
                "ProductDiscontinuedEvent",
                "DELETED from ProductPriceView"
        );
    }

    @KafkaHandler
    @Transactional
    public void handleProductPriceChangedEvent(ProductPriceChangedEvent productPriceChangedEvent) {
        if(processedEventRepo.existsById(productPriceChangedEvent.getEventId())) {
            return;
        }

        ProductPriceView existingProduct = productPriceViewRepo.findById(productPriceChangedEvent.getProductId()).get();
        existingProduct = new ProductPriceView(
                existingProduct.getProductId(),
                productPriceChangedEvent.getNewPrice()
        );
        productPriceViewRepo.save(existingProduct);

        processedEventRepo.save(new ProcessedEvent(productPriceChangedEvent.getEventId()));
        logEventPublisher(
                productPriceChangedEvent.getEventId(),
                productPriceChangedEvent.getProductId(),
                "ProductPriceChangedEvent",
                "UPDATED in ProductPriceView"
        );
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("Unhandled product event received: {}", event.getClass().getName());
    }
}
