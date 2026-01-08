package com.jagadesh.productCommandService.service;

import com.jagadesh.event.product.ProductCreatedEvent;
import com.jagadesh.event.product.ProductDiscontinuedEvent;
import com.jagadesh.event.product.ProductNameChangedEvent;
import com.jagadesh.event.product.ProductPriceChangedEvent;
import com.jagadesh.productCommandService.dto.ProductEvent;
import com.jagadesh.productCommandService.dto.ProductUpdateRequestDto;
import com.jagadesh.productCommandService.exception.ProductDoesNotExistException;
import com.jagadesh.productCommandService.model.Product;
import com.jagadesh.productCommandService.repository.ProductRepo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductCommandService {
    private ProductRepo productRepo;
    private ProductEventPublisher productEventPublisher;

    public ProductCommandService(
            ProductRepo productRepo,  ProductEventPublisher productEventPublisher) {
        this.productRepo = productRepo;
        this.productEventPublisher = productEventPublisher;
    }

    public Product addProduct(Product product) {
        Optional<Product> existingProduct = productRepo.findByName(product.getName());
        if(existingProduct.isPresent()){
            throw new ProductDoesNotExistException("Product with name " + product.getName() + " already exists");
        }

        Product newProduct = productRepo.save(product);

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                UUID.randomUUID().toString(),
                newProduct.getProductId(),
                newProduct.getName(),
                newProduct.getDescription(),
                newProduct.getPrice(),
                1,
                Instant.now()
        );
        productEventPublisher.publishProductCreatedEvent(productCreatedEvent);

        return newProduct;
    }

    public Product updateProduct(long id, ProductUpdateRequestDto updatedProduct) {
        Optional<Product> product = productRepo.findById(id);

        if(product.isEmpty()) {
            throw new ProductDoesNotExistException("product: "+id+" not found");
        }

        Product newlyUpdatedProduct;
        Product existingProduct = product.get();
        if(!updatedProduct.name().equals(existingProduct.getName())) {
            ProductNameChangedEvent productNameChangedEvent = new ProductNameChangedEvent(
                    UUID.randomUUID().toString(),
                    id,
                    existingProduct.getName(),
                    updatedProduct.name(),
                    1,
                    Instant.now()
            );
            existingProduct.setName(updatedProduct.name());
            newlyUpdatedProduct = productRepo.save(existingProduct);
            productEventPublisher.publishProductNameChangedEvent(productNameChangedEvent);
            return  newlyUpdatedProduct;
        }

        ProductPriceChangedEvent productPriceChangedEvent = new ProductPriceChangedEvent(
                UUID.randomUUID().toString(),
                id,
                existingProduct.getPrice(),
                updatedProduct.price(),
                1,
                Instant.now()
        );
        existingProduct.setPrice(updatedProduct.price());
        newlyUpdatedProduct = productRepo.save(existingProduct);
        productEventPublisher.publishProductPriceChangedEvent(productPriceChangedEvent);

        return newlyUpdatedProduct;
    }

    public String deleteProduct(String productName) {
        Optional<Product> existingProduct = productRepo.findByName(productName);

        if(existingProduct.isEmpty()) {
            throw new ProductDoesNotExistException("product: "+productName+" not found");
        }
        Product product = existingProduct.get();
        productRepo.delete(product);

        ProductDiscontinuedEvent productDiscontinuedEvent = new ProductDiscontinuedEvent(
                UUID.randomUUID().toString(),
                product.getProductId(),
                1,
                Instant.now()
        );
        productEventPublisher.publishProductDiscontinuedEvent(productDiscontinuedEvent);

        return "product: "+productName+" deleted successfully";
    }
}
