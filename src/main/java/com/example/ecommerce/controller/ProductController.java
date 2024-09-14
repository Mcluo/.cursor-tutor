package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
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
}