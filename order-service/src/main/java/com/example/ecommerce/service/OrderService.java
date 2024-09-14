package com.example.ecommerce.service;

import com.example.ecommerce.client.ProductClient;
import com.example.ecommerce.messaging.OrderProducer;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.repository.OrderRepository;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private OrderProducer orderProducer;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @GlobalTransactional
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        for (OrderItem item : order.getItems()) {
            boolean decreased = productClient.decreaseStock(item.getProduct().getId(), item.getQuantity());
            if (!decreased) {
                throw new IllegalStateException("Failed to decrease stock for product: " + item.getProduct().getId());
            }
        }
        Order savedOrder = orderRepository.save(order);
        orderProducer.orderCreated().send(MessageBuilder.withPayload(savedOrder).build());
        return savedOrder;
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @GlobalTransactional
    public Order cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }
        for (OrderItem item : order.getItems()) {
            productClient.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        orderProducer.orderCancelled().send(MessageBuilder.withPayload(cancelledOrder).build());
        return cancelledOrder;
    }
}