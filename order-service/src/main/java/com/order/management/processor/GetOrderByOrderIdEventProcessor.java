package com.order.management.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.order.management.event.GetOrderByOrderIdEvent;
import com.order.management.model.OrderItem;
import com.order.management.service.OrderService;

@Component
public class GetOrderByOrderIdEventProcessor {
    @Autowired
    OrderService orderService;

    @Async
    @EventListener
    public void handle(GetOrderByOrderIdEvent event) {
        List<OrderItem> orders = orderService.getOrder(event.getOrderItemId());
        event.setResult(orders);
    }
}
