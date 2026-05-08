package com.order.management;

import com.order.management.event.GetAllOrdersWithStatusEvent;
import com.order.management.model.OrderItem;
import com.order.management.model.OrderStatus;
import com.order.management.processor.GetAllOrdersWithStatusEventProcessor;
import com.order.management.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllOrdersWithStatusEventProcessorTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private GetAllOrdersWithStatusEventProcessor processor;

    @Test
    void handle_setsResult_withMatchingStatusOrders() {
        List<OrderItem> mockOrders = List.of(
                new OrderItem("ORD-001", "PROD-001", 2, "USER-001",
                        OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now()));
        when(orderService.getAllOrders("PENDING")).thenReturn(mockOrders);

        GetAllOrdersWithStatusEvent event = new GetAllOrdersWithStatusEvent("PENDING");
        processor.handle(event);

        assertEquals(mockOrders, event.getResult());
        verify(orderService, times(1)).getAllOrders("PENDING");
    }

    @Test
    void handle_setsEmptyList_whenNoOrdersMatchStatus() {
        when(orderService.getAllOrders("CANCELLED")).thenReturn(List.of());

        GetAllOrdersWithStatusEvent event = new GetAllOrdersWithStatusEvent("CANCELLED");
        processor.handle(event);

        assertNotNull(event.getResult());
        assertTrue(event.getResult().isEmpty());
        verify(orderService, times(1)).getAllOrders("CANCELLED");
    }

    @Test
    void handle_throwsException_whenServiceFails() {
        when(orderService.getAllOrders("PENDING")).thenThrow(new RuntimeException("DB error"));

        GetAllOrdersWithStatusEvent event = new GetAllOrdersWithStatusEvent("PENDING");

        assertThrows(RuntimeException.class, () -> processor.handle(event));
        verify(orderService, times(1)).getAllOrders("PENDING");
    }
}