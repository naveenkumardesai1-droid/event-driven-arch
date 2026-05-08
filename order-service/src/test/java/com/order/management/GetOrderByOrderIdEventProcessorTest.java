package com.order.management;

import com.order.management.event.GetOrderByOrderIdEvent;
import com.order.management.model.OrderItem;
import com.order.management.model.OrderStatus;
import com.order.management.processor.GetOrderByOrderIdEventProcessor;
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
class GetOrderByOrderIdEventProcessorTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private GetOrderByOrderIdEventProcessor processor;

    @Test
    void handle_setsResult_withOrderMatchingId() {
        List<OrderItem> mockOrders = List.of(
                new OrderItem("ORD-001", "PROD-001", 2, "USER-001",
                        OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now()));
        when(orderService.getOrder("ORD-001")).thenReturn(mockOrders);

        GetOrderByOrderIdEvent event = new GetOrderByOrderIdEvent("ORD-001");
        processor.handle(event);

        assertEquals(mockOrders, event.getResult());
        verify(orderService, times(1)).getOrder("ORD-001");
    }

    @Test
    void handle_setsEmptyList_whenOrderNotFound() {
        when(orderService.getOrder("ORD-999")).thenReturn(List.of());

        GetOrderByOrderIdEvent event = new GetOrderByOrderIdEvent("ORD-999");
        processor.handle(event);

        assertNotNull(event.getResult());
        assertTrue(event.getResult().isEmpty());
        verify(orderService, times(1)).getOrder("ORD-999");
    }

    @Test
    void handle_throwsException_whenServiceFails() {
        when(orderService.getOrder("ORD-001")).thenThrow(new RuntimeException("DB error"));

        GetOrderByOrderIdEvent event = new GetOrderByOrderIdEvent("ORD-001");

        assertThrows(RuntimeException.class, () -> processor.handle(event));
        verify(orderService, times(1)).getOrder("ORD-001");
    }
}