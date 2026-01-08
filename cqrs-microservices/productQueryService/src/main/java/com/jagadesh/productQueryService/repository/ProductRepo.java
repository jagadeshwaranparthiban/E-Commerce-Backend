package com.jagadesh.productQueryService.repository;

import com.jagadesh.productQueryService.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {
    Optional<Product> findByName(String productName);
}
