package com.jagadesh.orderservice.repository;

import com.jagadesh.orderservice.model.ProductPriceView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPriceViewRepo extends JpaRepository<ProductPriceView, Long> {
}
