package com.jagadesh.productCommandService.service;

import com.jagadesh.event.product.ProductCreatedEvent;
import com.jagadesh.event.product.ProductDiscontinuedEvent;
import com.jagadesh.event.product.ProductNameChangedEvent;
import com.jagadesh.event.product.ProductPriceChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductEventPublisher {

    private KafkaTemplate<String, Object> kafkaTemplate;

    public ProductEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private void logEventPublished(Object event, Long productId, String eventId) {
        log.info(
                "eventId={} productId={} eventType={} action=PUBLISHED",
                eventId,
                productId,
                event.getClass().getSimpleName()
        );
    }

    public void publishProductCreatedEvent(ProductCreatedEvent productCreatedEvent) {
        kafkaTemplate.send(
                "product-event",
                String.valueOf(productCreatedEvent.getProductId()),
                productCreatedEvent
        );
        logEventPublished(productCreatedEvent, productCreatedEvent.getProductId(), productCreatedEvent.getClass().getSimpleName());
    }

    public void publishProductPriceChangedEvent(ProductPriceChangedEvent productPriceChangedEvent) {
        kafkaTemplate.send(
                "product-event",
                String.valueOf(productPriceChangedEvent.getProductId()),
                productPriceChangedEvent
        );
        logEventPublished(productPriceChangedEvent, productPriceChangedEvent.getProductId(), productPriceChangedEvent.getClass().getSimpleName());
    }

    public void publishProductNameChangedEvent(ProductNameChangedEvent productNameChangedEvent) {
        kafkaTemplate.send(
                "product-event",
                String.valueOf(productNameChangedEvent.getProductId()),
                productNameChangedEvent
        );
        logEventPublished(productNameChangedEvent, productNameChangedEvent.getProductId(), productNameChangedEvent.getClass().getSimpleName());
    }

    public void publishProductDiscontinuedEvent(ProductDiscontinuedEvent productDiscontinuedEvent) {
        kafkaTemplate.send(
                "product-event",
                String.valueOf(productDiscontinuedEvent.getProductId()),
                productDiscontinuedEvent
        );
        logEventPublished(productDiscontinuedEvent, productDiscontinuedEvent.getProductId(), productDiscontinuedEvent.getClass().getSimpleName());
    }
}
