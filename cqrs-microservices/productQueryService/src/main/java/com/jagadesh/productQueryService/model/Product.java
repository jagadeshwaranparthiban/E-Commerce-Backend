package com.jagadesh.productQueryService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product_query")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private long productId;
    private String name;
    private String description;
    private BigDecimal price;
}
