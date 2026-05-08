package com.order.management.model;

import java.time.LocalDateTime;

public record OrderItem(String orderItemId, String productId, int quantity, String userId, OrderStatus status,
                LocalDateTime createdAt, LocalDateTime lastUpdatedAt) {
}

