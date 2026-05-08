package com.order.management.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.order.management.event.OrderStatusUpdateEvent;
import com.order.management.service.OrderService;

@Component
public class OrderStatusUpdateEventProcessor {
    @Autowired
    OrderService orderService;

    @Async
    @EventListener
    public void handleOrderStatusUpdateEvent(OrderStatusUpdateEvent event) {
        orderService.updateOrderStatus(event.timestamp(), event.orderStatus());
    }
}
