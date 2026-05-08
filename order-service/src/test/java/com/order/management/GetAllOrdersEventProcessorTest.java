package com.order.management;

import com.order.management.event.GetAllOrdersEvent;
import com.order.management.model.OrderItem;
import com.order.management.model.OrderStatus;
import com.order.management.processor.GetAllOrdersEventProcessor;
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
class GetAllOrdersEventProcessorTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private GetAllOrdersEventProcessor processor;

    @Test
    void handle_setsResultOnEvent_withOrdersFromService() {
        List<OrderItem> mockOrders = List.of(
                new OrderItem("ORD-001", "PROD-001", 2, "USER-001",
                        OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now()));
        when(orderService.getAllOrders()).thenReturn(mockOrders);

        GetAllOrdersEvent event = new GetAllOrdersEvent();
        processor.handle(event);

        assertEquals(mockOrders, event.getResult());
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void handle_setsEmptyList_whenNoOrdersExist() {
        when(orderService.getAllOrders()).thenReturn(List.of());

        GetAllOrdersEvent event = new GetAllOrdersEvent();
        processor.handle(event);

        assertNotNull(event.getResult());
        assertTrue(event.getResult().isEmpty());
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void handle_throwsException_whenServiceFails() {
        when(orderService.getAllOrders()).thenThrow(new RuntimeException("DB error"));

        GetAllOrdersEvent event = new GetAllOrdersEvent();

        assertThrows(RuntimeException.class, () -> processor.handle(event));
        verify(orderService, times(1)).getAllOrders();
    }
}