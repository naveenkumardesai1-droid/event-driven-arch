package com.order.management.processor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.order.management.KafkaPublisher;
import com.order.management.event.PersistOrderItemEvent;
import com.order.management.model.OrderItem;
import com.order.management.model.OrderItemBuilder;
import com.order.management.model.OrderStatus;
import com.order.management.service.OrderService;

@Component
public class PersistOrderItemEventProcessor {
  private KafkaPublisher kafkaPublisher;

  @Autowired
  OrderService orderService;

  @Autowired
  ApplicationEventPublisher eventPublisher;

  @Autowired
  public void setKafkaPublisher(KafkaPublisher kafkaPublisher) {
    this.kafkaPublisher = kafkaPublisher;
  }

  @Async
  @EventListener
  @Transactional
  public void handlePersistOrderItemEvent(PersistOrderItemEvent event) {
    List<OrderItem> orderItems = List.of();
    try {
       orderItems = event.orderItems().stream().map(item -> {
        return OrderItemBuilder.anOrderItemBuilder()
            .withOrderItemId(UUID.randomUUID().toString())
            .withProductId(item.productId())
            .withQuantity(item.quantity())
            .withUserId(event.userId())
            .withStatus(OrderStatus.PENDING)
            .withCreatedAt(LocalDateTime.now())
            .withLastUpdatedAt(LocalDateTime.now())
            .build();
      }).toList();

      orderService.saveOrderItems(orderItems);
    } catch (Exception e) {
      throw new RuntimeException("Failed to persist order items: " + e.getMessage(), e);
    }
    kafkaPublisher.publish("orders", orderItems);
  }
}
