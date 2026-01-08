package com.jagadesh.productQueryService.controller;

import com.jagadesh.productQueryService.model.Product;
import com.jagadesh.productQueryService.service.ProductQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("query/products")
public class ProductQueryController {

    private ProductQueryService productQueryService;
    public ProductQueryController(ProductQueryService productQueryService) {
        this.productQueryService = productQueryService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<Product>> getProducts(){
        return ResponseEntity.ok(productQueryService.getAllProducts());
    }

    @GetMapping("/get_id/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") long id){
        return ResponseEntity.ok(productQueryService.getProductById(id));
    }

    @GetMapping("/get_name/{name}")
    public ResponseEntity<Product> getProductByName(@PathVariable("name") String name){
        return ResponseEntity.ok(productQueryService.getProductByName(name));
    }
}
