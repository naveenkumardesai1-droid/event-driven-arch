package com.order.management.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.order.management.event.GetAllOrdersWithStatusEvent;
import com.order.management.model.OrderItem;
import com.order.management.service.OrderService;

@Component
public class GetAllOrdersWithStatusEventProcessor {
    @Autowired
    OrderService orderService;

    @Async
    @EventListener
    public void handle(GetAllOrdersWithStatusEvent event) {
        List<OrderItem> orders = orderService.getAllOrders(event.getStatus());
        event.setResult(orders);
    }
}
