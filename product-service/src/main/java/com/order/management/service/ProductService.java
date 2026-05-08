package com.order.management.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.management.events.NotificationEvent;
import com.order.management.model.OrderItem;

@Service
public class ProductService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ProductService(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            ApplicationEventPublisher eventPublisher) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void updateProductStock(OrderItem item) {
        String sql = "UPDATE product SET stock = stock - :quantity WHERE product_id = :productId AND stock >= :quantity";
        int rowsUpdated = namedParameterJdbcTemplate.update(sql, Map.of("quantity", item.quantity(), "productId", item.productId()));
        if (rowsUpdated == 0) {
            throw new RuntimeException("Product not found with ID: " + item.productId() + " or insufficient stock");
        }
        eventPublisher.publishEvent(new NotificationEvent(item.orderItemId(),item.userId()));
    }
}
