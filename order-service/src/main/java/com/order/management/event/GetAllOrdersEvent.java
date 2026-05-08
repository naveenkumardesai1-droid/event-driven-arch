package com.order.management.event;

import java.util.List;

import com.order.management.model.OrderItem;

public class GetAllOrdersEvent {
    private List<OrderItem> result;

    public List<OrderItem> getResult() {
        return result;
    }

    public void setResult(List<OrderItem> result) {
        this.result = result;
    }
}
