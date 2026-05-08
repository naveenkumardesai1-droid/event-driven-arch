package com.order.management;

import com.order.management.event.CancelOrderEvent;
import com.order.management.processor.CancelOrderEventProcessor;
import com.order.management.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CancelOrderEventProcessorTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CancelOrderEventProcessor cancelOrderEventProcessor;

    @Test
    void handle_callsCancelOrder_withCorrectOrderItemId() {
        CancelOrderEvent event = new CancelOrderEvent("ORD-001");

        cancelOrderEventProcessor.handle(event);

        verify(orderService, times(1)).cancelOrder("ORD-001");
    }

    @Test
    void handle_throwsException_whenCancelOrderFails() {
        CancelOrderEvent event = new CancelOrderEvent("ORD-001");
        doThrow(new RuntimeException("Cancel failed")).when(orderService).cancelOrder("ORD-001");

        assertThrows(RuntimeException.class, () -> cancelOrderEventProcessor.handle(event));

        verify(orderService, times(1)).cancelOrder("ORD-001");
    }
}