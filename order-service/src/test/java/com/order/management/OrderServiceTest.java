package com.order.management;

import com.order.management.model.OrderItem;
import com.order.management.model.OrderStatus;
import com.order.management.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private OrderService orderService;

    private OrderItem sampleOrderItem;

    @BeforeEach
    void setUp() {
        sampleOrderItem = new OrderItem(
                "ORD-001",
                "PROD-001",
                2,
                "USER-001",
                OrderStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now());
    }


    @Test
    void saveOrderItems_success() {
        when(jdbcTemplate.batchUpdate(anyString(), any(SqlParameterSource[].class)))
                .thenReturn(new int[] { 1 });

        assertDoesNotThrow(() -> orderService.saveOrderItems(List.of(sampleOrderItem)));

        verify(jdbcTemplate, times(1)).batchUpdate(anyString(), any(SqlParameterSource[].class));
    }

    @Test
    void saveOrderItems_throwsRuntimeException_onFailure() {
        when(jdbcTemplate.batchUpdate(anyString(), any(SqlParameterSource[].class)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.saveOrderItems(List.of(sampleOrderItem)));

        assertTrue(ex.getMessage().contains("Failed to save order items"));
    }


    @Test
    void updateOrderStatus_success() {
        when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

        assertDoesNotThrow(() -> orderService.updateOrderStatus(LocalDateTime.now(), OrderStatus.CANCELLED));

        verify(jdbcTemplate, times(1)).update(anyString(), any(SqlParameterSource.class));
    }

    @Test
    void updateOrderStatus_throwsRuntimeException_onFailure() {
        when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus(LocalDateTime.now(), OrderStatus.CANCELLED));

        assertTrue(ex.getMessage().contains("Failed to update order status"));
    }


    @Test
    void getAllOrders_returnsListOfOrders() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<OrderItem>>any()))
                .thenReturn(List.of(sampleOrderItem));

        List<OrderItem> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-001", result.get(0).orderItemId());
    }

    @Test
    void getAllOrders_throwsRuntimeException_onFailure() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<OrderItem>>any()))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.getAllOrders());

        assertTrue(ex.getMessage().contains("Failed to fetch all orders"));
    }


    @Test
    void getAllOrdersWithStatus_returnsFilteredOrders() {
        when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class),
                ArgumentMatchers.<RowMapper<OrderItem>>any()))
                .thenReturn(List.of(sampleOrderItem));

        List<OrderItem> result = orderService.getAllOrders("PENDING");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.PENDING, result.get(0).status());
    }

    @Test
    void getAllOrdersWithStatus_throwsRuntimeException_onFailure() {
        when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class),
                ArgumentMatchers.<RowMapper<OrderItem>>any()))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.getAllOrders("PENDING"));

        assertTrue(ex.getMessage().contains("Failed to fetch all orders"));
    }


    @Test
    void cancelOrder_success() {
        when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

        assertDoesNotThrow(() -> orderService.cancelOrder("ORD-001"));

        verify(jdbcTemplate, times(1)).update(anyString(), any(SqlParameterSource.class));
    }

    @Test
    void cancelOrder_throwsRuntimeException_whenNoPendingOrderFound() {
        when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class))).thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder("ORD-001"));

        assertTrue(ex.getMessage().contains("No order found with status PENDING"));
    }

    @Test
    void cancelOrder_throwsRuntimeException_onDbFailure() {
        when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> orderService.cancelOrder("ORD-001"));
    }


    @Test
    void getOrder_returnsOrder() {
        when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class),
                ArgumentMatchers.<RowMapper<OrderItem>>any()))
                .thenReturn(List.of(sampleOrderItem));

        List<OrderItem> result = orderService.getOrder("ORD-001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-001", result.get(0).orderItemId());
    }

    @Test
    void getOrder_throwsRuntimeException_onFailure() {
        when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class),
                ArgumentMatchers.<RowMapper<OrderItem>>any()))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.getOrder("ORD-001"));

        assertTrue(ex.getMessage().contains("Failed to fetch order by ID"));
    }
}