package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return ResponseEntity.ok(productService.updateProduct(id, productDetails));
    }

    @PostMapping("/{id}/decrease-stock")
    public ResponseEntity<String> decreaseStock(@PathVariable Long id, @RequestParam int quantity) {
        boolean success = productService.decreaseStock(id, quantity);
        if (success) {
            return ResponseEntity.ok("Stock decreased successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to decrease stock");
        }
    }

    @PostMapping("/{id}/increase-stock")
    public ResponseEntity<String> increaseStock(@PathVariable Long id, @RequestParam int quantity) {
        boolean success = productService.increaseStock(id, quantity);
        if (success) {
            return ResponseEntity.ok("Stock increased successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to increase stock");
        }
    }
}