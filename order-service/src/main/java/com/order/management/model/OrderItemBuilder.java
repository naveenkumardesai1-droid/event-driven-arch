package com.order.management.model;

import java.time.LocalDateTime;

public class OrderItemBuilder {
    private String orderItemId;
    private String productId;
    private int quantity;
    private String userId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    private OrderItemBuilder() {
    }

    public static OrderItemBuilder anOrderItemBuilder() {
        return new OrderItemBuilder();
    }

    public OrderItemBuilder withOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
        return this;
    }

    public OrderItemBuilder withProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public OrderItemBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderItemBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public OrderItemBuilder withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderItemBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public OrderItemBuilder withLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
        return this;
    }

    public OrderItem build() {
        return new OrderItem(orderItemId, productId, quantity, userId, status, createdAt, lastUpdatedAt);
    }
}
