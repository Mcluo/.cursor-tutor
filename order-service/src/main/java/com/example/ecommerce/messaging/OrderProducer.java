package com.example.ecommerce.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrderProducer {
    String ORDER_CREATED = "orderCreated";
    String ORDER_CANCELLED = "orderCancelled";

    @Output(ORDER_CREATED)
    MessageChannel orderCreated();

    @Output(ORDER_CANCELLED)
    MessageChannel orderCancelled();
}