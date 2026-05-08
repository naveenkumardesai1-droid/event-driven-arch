package com.order.management.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.kafka.clients.consumer.internals.events.ApplicationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.management.event.InventoryEvent;
import com.order.management.model.OrderItem;
import com.order.management.model.OrderStatus;

@Service
public class OrderService {
    private static final String INSERT_SQL = "INSERT INTO order_item (order_item_id, product_id, quantity, last_update_at, user_id, status, created_at) "
            +
            "VALUES (:orderItemId, :productId, :quantity, :lastUpdatedAt, :userId, :status, :createdAt)";

    private static final String UPDATE_SQL = "UPDATE order_item SET status = :status, last_update_at = :lastUpdatedAt WHERE status = 'PENDING'";

    private static final String SELECT_ALL_ORDERS_SQL = "SELECT * FROM order_item";

    private static final String SELECT_ORDER_STATUS_SQL = "SELECT * FROM order_item WHERE status=:status";

    private static final String CANCEL_ORDER_SQL = "UPDATE order_item SET status = :status, last_update_at = :lastUpdatedAt WHERE status = 'PENDING' and order_item_id = :orderItemId";

    private static final String SELECT_ORDER_BY_ID_SQL = "SELECT * FROM order_item WHERE order_item_id=:orderItemId";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OrderService(NamedParameterJdbcTemplate jdbcTemplate, ApplicationEventPublisher eventPublisher) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void saveOrderItems(List<OrderItem> orderItems) {
        SqlParameterSource[] batch = orderItems.stream()
                .map(item -> new MapSqlParameterSource()
                        .addValue("orderItemId", item.orderItemId())
                        .addValue("productId", item.productId())
                        .addValue("quantity", item.quantity())
                        .addValue("userId", item.userId())
                        .addValue("status", item.status().name())
                        .addValue("createdAt", item.createdAt())
                        .addValue("lastUpdatedAt", item.lastUpdatedAt()))
                .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(INSERT_SQL, batch);

        eventPublisher.publishEvent(new InventoryEvent(orderItems));
    }
    
    @Transactional
    public void updateOrderStatus(LocalDateTime lastUpdatedAt, OrderStatus status) {
        try {
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("status", status.name())
                    .addValue("lastUpdatedAt", lastUpdatedAt);

            jdbcTemplate.update(UPDATE_SQL, params);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update order status: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getAllOrders() {
        try {
            return jdbcTemplate.query(SELECT_ALL_ORDERS_SQL, (rs, rowNum) -> new OrderItem(
                    rs.getString("order_item_id"),
                    rs.getString("product_id"),
                    rs.getInt("quantity"),
                    rs.getString("user_id"),
                    OrderStatus.valueOf(rs.getString("status")),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("last_update_at") != null ? rs.getTimestamp("last_update_at").toLocalDateTime()
                            : LocalDateTime.now()));

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all orders: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getAllOrders(String status) {
        try {
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("status", status);
            return jdbcTemplate.query(SELECT_ORDER_STATUS_SQL, params, (rs, rowNum) -> new OrderItem(
                    rs.getString("order_item_id"),
                    rs.getString("product_id"),
                    rs.getInt("quantity"),
                    rs.getString("user_id"),
                    OrderStatus.valueOf(rs.getString("status")),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("last_update_at") != null ? rs.getTimestamp("last_update_at").toLocalDateTime()
                            : LocalDateTime.now()));

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all orders: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void cancelOrder(String orderItemId) {
        try {
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("status", OrderStatus.CANCELLED.name())
                    .addValue("lastUpdatedAt", LocalDateTime.now())
                    .addValue("orderItemId", orderItemId);

            int affectedRows = jdbcTemplate.update(CANCEL_ORDER_SQL, params);
            if (affectedRows == 0) {
                throw new RuntimeException(
                        "No order found with status PENDING to cancel for orderItemId: " + orderItemId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getOrder(String orderItemId) {
        try {
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("orderItemId", orderItemId);
            return jdbcTemplate.query(SELECT_ORDER_BY_ID_SQL, params, (rs, rowNum) -> new OrderItem(
                    rs.getString("order_item_id"),
                    rs.getString("product_id"),
                    rs.getInt("quantity"),
                    rs.getString("user_id"),
                    OrderStatus.valueOf(rs.getString("status")),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("last_update_at") != null ? rs.getTimestamp("last_update_at").toLocalDateTime()
                            : LocalDateTime.now()));

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch order by ID: " + e.getMessage(), e);
        }
    }
}
