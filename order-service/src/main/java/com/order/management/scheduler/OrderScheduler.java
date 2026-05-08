package com.order.management.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.order.management.event.OrderStatusUpdateEvent;
import com.order.management.model.OrderStatus;

@Component
public class OrderScheduler {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 300000) // every 5 minutes in milliseconds
    public void updateStatus() {
      //  eventPublisher.publishEvent(new OrderStatusUpdateEvent(LocalDateTime.now(), OrderStatus.PROCESSING));
    }
}
