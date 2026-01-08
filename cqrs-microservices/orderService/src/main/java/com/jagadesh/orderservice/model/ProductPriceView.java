package com.jagadesh.orderservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "product_price_view")
public class ProductPriceView {
    @Id
    private long productId;
    private BigDecimal price;

    public ProductPriceView() {}

    public ProductPriceView(long productId, BigDecimal price) {
        this.productId = productId;
        this.price = price;
    }

    public long getProductId() {
        return productId;
    }

    public BigDecimal getPrice() {
        return price;
    }
}

