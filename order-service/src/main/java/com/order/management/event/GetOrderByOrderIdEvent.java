package com.order.management.event;

import java.util.List;

import com.order.management.model.OrderItem;

public class GetOrderByOrderIdEvent {
    private List<OrderItem> result;
    private final String orderItemId;

    public GetOrderByOrderIdEvent(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public List<OrderItem> getResult() {
        return result;
    }

    public void setResult(List<OrderItem> result) {
        this.result = result;
    }

    public String getOrderItemId() {
        return orderItemId;
    }
}
