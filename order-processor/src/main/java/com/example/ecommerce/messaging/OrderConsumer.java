package com.example.ecommerce.messaging;

import com.example.ecommerce.model.Order;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @StreamListener(Sink.INPUT)
    public void handleOrderCreated(Order order) {
        // 处理订单创建后的异步任务，例如发送确认邮件
        System.out.println("Order created: " + order.getId());
        // TODO: 实现发送确认邮件的逻辑
    }
}