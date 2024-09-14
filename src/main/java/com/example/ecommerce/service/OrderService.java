package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order createOrder(Order order) {
        // 检查库存并减少库存
        for (OrderItem item : order.getItems()) {
            Product product = productService.getProduct(item.getProduct().getId());
            if (product == null || product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + item.getProduct().getId());
            }
            productService.decreaseStock(item.getProduct().getId(), item.getQuantity());
        }
        return orderRepository.save(order);
    }

    // 其他订单相关方法
}