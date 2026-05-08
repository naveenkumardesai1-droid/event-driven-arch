package com.order.management.event;

import java.time.LocalDateTime;

import com.order.management.model.OrderStatus;

public record OrderStatusUpdateEvent(LocalDateTime timestamp, OrderStatus orderStatus) {
}
