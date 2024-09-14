package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Product getProduct(Long id) {
        String key = "product:" + id;
        Product product = (Product) redisTemplate.opsForValue().get(key);
        if (product == null) {
            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isPresent()) {
                product = optionalProduct.get();
                redisTemplate.opsForValue().set(key, product, 1, TimeUnit.HOURS);
            }
        }
        return product;
    }

    @Transactional
    public boolean decreaseStock(Long id, int quantity) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getStock() >= quantity) {
                product.setStock(product.getStock() - quantity);
                productRepository.save(product);
                String key = "product:" + id;
                redisTemplate.delete(key);
                return true;
            }
        }
        return false;
    }
}