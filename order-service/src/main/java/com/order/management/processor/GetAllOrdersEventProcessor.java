package com.order.management.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.order.management.event.GetAllOrdersEvent;
import com.order.management.service.OrderService;

@Component
public class GetAllOrdersEventProcessor {
    @Autowired
    OrderService orderService;

    @Async
    @EventListener
    public void handle(GetAllOrdersEvent event) {
        var orders = orderService.getAllOrders();
        event.setResult(orders);
    }
}
