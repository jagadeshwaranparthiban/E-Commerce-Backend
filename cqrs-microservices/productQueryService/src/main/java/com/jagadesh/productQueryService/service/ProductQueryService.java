package com.jagadesh.productQueryService.service;

import com.jagadesh.productQueryService.exception.ProductDoesNotExistException;
import com.jagadesh.productQueryService.model.Product;
import com.jagadesh.productQueryService.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductQueryService {
    private ProductRepo productRepo;

    public ProductQueryService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(Long id) {
        Optional<Product> optional = productRepo.findById(id);
        if(optional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        return optional.get();
    }

    public Product getProductByName(String productName) {
        Optional<Product> optional = productRepo.findByName(productName);
        if(optional.isEmpty()) {
            throw new ProductDoesNotExistException("Product: "+productName+" not found");
        }
        return optional.get();
    }

}
