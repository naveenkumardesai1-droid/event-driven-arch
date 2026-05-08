package com.order.management;

import com.order.management.event.OrderStatusUpdateEvent;
import com.order.management.model.OrderStatus;
import com.order.management.processor.OrderStatusUpdateEventProcessor;
import com.order.management.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusUpdateEventProcessorTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderStatusUpdateEventProcessor processor;

    @Test
    void handleOrderStatusUpdateEvent_callsUpdateOrderStatus_withCorrectParams() {
        LocalDateTime timestamp = LocalDateTime.now();
        OrderStatusUpdateEvent event = new OrderStatusUpdateEvent(timestamp, OrderStatus.CANCELLED);

        processor.handleOrderStatusUpdateEvent(event);

        verify(orderService, times(1)).updateOrderStatus(timestamp, OrderStatus.CANCELLED);
    }

    @Test
    void handleOrderStatusUpdateEvent_throwsException_whenServiceFails() {
        LocalDateTime timestamp = LocalDateTime.now();
        OrderStatusUpdateEvent event = new OrderStatusUpdateEvent(timestamp, OrderStatus.CANCELLED);

        doThrow(new RuntimeException("DB error"))
                .when(orderService).updateOrderStatus(timestamp, OrderStatus.CANCELLED);

        assertThrows(RuntimeException.class,
                () -> processor.handleOrderStatusUpdateEvent(event));

        verify(orderService, times(1)).updateOrderStatus(timestamp, OrderStatus.CANCELLED);
    }
}