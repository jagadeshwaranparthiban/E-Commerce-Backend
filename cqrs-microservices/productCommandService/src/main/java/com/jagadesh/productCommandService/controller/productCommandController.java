package com.jagadesh.productCommandService.controller;

import com.jagadesh.productCommandService.dto.ProductEvent;
import com.jagadesh.productCommandService.dto.ProductUpdateRequestDto;
import com.jagadesh.productCommandService.model.Product;
import com.jagadesh.productCommandService.service.ProductCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class productCommandController {

    private ProductCommandService productService;

    public productCommandController(ProductCommandService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product){
        return ResponseEntity.ok(productService.addProduct(product));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") long id, @RequestBody ProductUpdateRequestDto product){
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/remove/{name}")
    public ResponseEntity<String> deleteProduct(@PathVariable("name") String name){
        return ResponseEntity.ok(productService.deleteProduct(name));
    }
}
