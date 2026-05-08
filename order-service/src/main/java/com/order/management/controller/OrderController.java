package com.order.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.order.management.event.CancelOrderEvent;
import com.order.management.event.GetAllOrdersEvent;
import com.order.management.event.GetAllOrdersWithStatusEvent;
import com.order.management.event.GetOrderByOrderIdEvent;
import com.order.management.event.PersistOrderItemEvent;
import com.order.management.model.OrderItem;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/placeorder")
    public ResponseEntity<?> placeOrder(@RequestHeader("userId") String userId,
            @RequestBody List<OrderItem> orderItems) {
        try {
            eventPublisher.publishEvent(new PersistOrderItemEvent(userId, orderItems));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to place order: " + e.getMessage());
        }
        return ResponseEntity.ok("Order placed successfully!");
    }

    @GetMapping
    public ResponseEntity<List<OrderItem>> getOrders(@RequestParam(required = false) String status) {
        try {
            if (status != null) {
                GetAllOrdersWithStatusEvent getAllOrdersWithStatusEvent = new GetAllOrdersWithStatusEvent(status);
                eventPublisher.publishEvent(getAllOrdersWithStatusEvent);
                return ResponseEntity.ok().body(getAllOrdersWithStatusEvent.getResult());
            } else {
                GetAllOrdersEvent allOrdersEvent = new GetAllOrdersEvent();
                eventPublisher.publishEvent(allOrdersEvent);
                return ResponseEntity.ok().body(allOrdersEvent.getResult());
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/cancelorder")
    public ResponseEntity<?> cancelOrder(@RequestBody String orderItemId) {
        try {
            eventPublisher.publishEvent(new CancelOrderEvent(orderItemId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to cancel order: " + e.getMessage());
        }
        return ResponseEntity.ok("Order cancelled successfully!");
    }

    @GetMapping("{id}")
    public ResponseEntity<List<OrderItem>> getOrderById(@PathVariable("id") String orderItemId) {
        try {
            GetOrderByOrderIdEvent getOrderByOrderIdEvent = new GetOrderByOrderIdEvent(orderItemId);
            eventPublisher.publishEvent(getOrderByOrderIdEvent);
            return ResponseEntity.ok().body(getOrderByOrderIdEvent.getResult());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}