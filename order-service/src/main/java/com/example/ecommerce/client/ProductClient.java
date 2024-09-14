package com.example.ecommerce.client;

import com.example.ecommerce.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    Product getProduct(@PathVariable Long id);

    @PostMapping("/api/products/{id}/decrease-stock")
    boolean decreaseStock(@PathVariable Long id, @RequestParam int quantity);
}