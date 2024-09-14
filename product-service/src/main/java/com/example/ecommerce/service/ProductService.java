package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        
        return productRepository.save(product);
    }

    @TwoPhaseBusinessAction(name = "decreaseStock", commitMethod = "commitStock", rollbackMethod = "rollbackStock")
    public boolean decreaseStock(@BusinessActionContextParameter(paramName = "productId") Long productId,
                                 @BusinessActionContextParameter(paramName = "quantity") int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        if (product.getStock() < quantity) {
            return false;
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        return true;
    }

    public boolean commitStock(BusinessActionContext context) {
        // 提交操作，通常不需要做任何事
        return true;
    }

    public boolean rollbackStock(BusinessActionContext context) {
        Long productId = Long.parseLong(context.getActionContext("productId").toString());
        int quantity = Integer.parseInt(context.getActionContext("quantity").toString());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
        return true;
    }

    @Transactional
    public boolean increaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
        return true;
    }
}