package com.jagadesh.productQueryService.service;

import com.jagadesh.event.product.ProductCreatedEvent;
import com.jagadesh.event.product.ProductDiscontinuedEvent;
import com.jagadesh.event.product.ProductNameChangedEvent;
import com.jagadesh.event.product.ProductPriceChangedEvent;
import com.jagadesh.productQueryService.exception.NonRetryableEventException;
import com.jagadesh.productQueryService.model.ProcessedEvent;
import com.jagadesh.productQueryService.model.Product;
import com.jagadesh.productQueryService.repository.ProcessedEventRepo;
import com.jagadesh.productQueryService.repository.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@KafkaListener(
        topics = "product-event",
        groupId = "product-group"
)
@Service
@Slf4j
public class ProductEventHandler {

    private ProductRepo productRepo;
    private ProcessedEventRepo processedEventRepo;

    public ProductEventHandler(ProductRepo productRepo, ProcessedEventRepo processedEventRepo) {
        this.productRepo = productRepo;
        this.processedEventRepo = processedEventRepo;
    }

    private void logEventPublished(Object event, Long productId, String eventId) {
        log.info(
                "eventId={} productId={} eventType={} action=PUBLISHED",
                eventId,
                productId,
                event.getClass().getSimpleName()
        );
    }

    @KafkaHandler
    @Transactional
    public void processProductCreatedEvent(ProductCreatedEvent productCreatedEvent) {

        if(processedEventRepo.existsById(productCreatedEvent.getEventId())) {
            return;
        }

        Product newProduct = new Product();
        newProduct.setProductId(productCreatedEvent.getProductId());
        newProduct.setName(productCreatedEvent.getName());
        newProduct.setDescription(productCreatedEvent.getDescription());
        newProduct.setPrice(productCreatedEvent.getPrice());

        productRepo.save(newProduct);
        processedEventRepo.save(new ProcessedEvent(productCreatedEvent.getEventId()));
        logEventPublished(productCreatedEvent.getEventId(), productCreatedEvent.getProductId(), productCreatedEvent.getEventId());
    }

    @KafkaHandler
    @Transactional
    public void processProductPriceChangedEvent(ProductPriceChangedEvent productPriceChangedEvent) {

        if(processedEventRepo.existsById(productPriceChangedEvent.getEventId())) {
            return;
        }

        Product product = productRepo.findById(productPriceChangedEvent.getProductId())
                .orElseThrow(()-> new NonRetryableEventException(
                        "Product not found for the event "+productPriceChangedEvent.getEventId()
                ));

        product.setPrice(productPriceChangedEvent.getNewPrice());

        productRepo.save(product);
        processedEventRepo.save(new ProcessedEvent(productPriceChangedEvent.getEventId()));
        logEventPublished(productPriceChangedEvent.getEventId(), productPriceChangedEvent.getProductId(), productPriceChangedEvent.getEventId());
    }

    @KafkaHandler
    @Transactional
    public void processProductDiscontinuedEvent(ProductDiscontinuedEvent productDiscontinuedEvent) {

        if(processedEventRepo.existsById(productDiscontinuedEvent.getEventId())) {
            return;
        }

        Product product = productRepo.findById(productDiscontinuedEvent.getProductId())
                .orElseThrow(()-> new NonRetryableEventException(
                        "Product not found for the event "+productDiscontinuedEvent.getEventId()
                ));

        productRepo.delete(product);
        processedEventRepo.save(new ProcessedEvent(productDiscontinuedEvent.getEventId()));
        logEventPublished(productDiscontinuedEvent.getEventId(), productDiscontinuedEvent.getProductId(), productDiscontinuedEvent.getEventId());
    }

    @KafkaHandler
    @Transactional
    public void processProductNameChangedEvent(ProductNameChangedEvent productNameChangedEvent) {

        if(processedEventRepo.existsById(productNameChangedEvent.getEventId())) {
            return;
        }

        Product product = productRepo.findById(productNameChangedEvent.getProductId())
                .orElseThrow(()-> new NonRetryableEventException(
                        "Product not found for the event "+productNameChangedEvent.getEventId()
                ));

        product.setName(productNameChangedEvent.getNewName());

        productRepo.save(product);
        processedEventRepo.save(new ProcessedEvent(productNameChangedEvent.getEventId()));
        logEventPublished(productNameChangedEvent.getEventId(), productNameChangedEvent.getProductId(), productNameChangedEvent.getEventId());
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn(
                "action=UNKNOWN_EVENT_RECEIVED payloadType={} payload={}",
                event.getClass().getName(),
                event
        );
    }
}
