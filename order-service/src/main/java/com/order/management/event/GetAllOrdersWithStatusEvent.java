package com.order.management.event;

import java.util.List;

import com.order.management.model.OrderItem;

public class GetAllOrdersWithStatusEvent {
    private List<OrderItem> result;
    private final String status;

    public GetAllOrdersWithStatusEvent(String status) {
        this.status = status;
    }

    public List<OrderItem> getResult() {
        return result;
    }

    public void setResult(List<OrderItem> result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }
}