package com.order.management.event;

import java.util.List;

import com.order.management.model.OrderItem;

public record InventoryEvent(List<OrderItem> orderItems) {
}
