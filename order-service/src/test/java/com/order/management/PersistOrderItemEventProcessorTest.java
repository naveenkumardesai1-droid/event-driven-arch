package com.order.management;

import com.order.management.event.PersistOrderItemEvent;
import com.order.management.model.OrderItem;
import com.order.management.model.OrderStatus;
import com.order.management.processor.PersistOrderItemEventProcessor;
import com.order.management.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersistOrderItemEventProcessorTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PersistOrderItemEventProcessor processor;

    @Test
    void handlePersistOrderItemEvent_savesOrderItems_withCorrectFields() {
        OrderItem inputItem = new OrderItem(null, "PROD-001", 3, null,
                OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());

        PersistOrderItemEvent event = new PersistOrderItemEvent("USER-001", List.of(inputItem));

        processor.handlePersistOrderItemEvent(event);

        ArgumentCaptor<List<OrderItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderService, times(1)).saveOrderItems(captor.capture());

        List<OrderItem> savedItems = captor.getValue();
        assertEquals(1, savedItems.size());

        OrderItem saved = savedItems.get(0);
        assertNotNull(saved.orderItemId()); // UUID generated
        assertEquals("PROD-001", saved.productId());
        assertEquals(3, saved.quantity());
        assertEquals("USER-001", saved.userId());
        assertEquals(OrderStatus.PENDING, saved.status());
        assertNotNull(saved.createdAt());
        assertNotNull(saved.lastUpdatedAt());
    }

    @Test
    void handlePersistOrderItemEvent_savesMultipleOrderItems() {
        List<OrderItem> inputItems = List.of(
                new OrderItem(null, "PROD-001", 1, null, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now()),
                new OrderItem(null, "PROD-002", 2, null, OrderStatus.PENDING, LocalDateTime.now(),
                        LocalDateTime.now()));

        PersistOrderItemEvent event = new PersistOrderItemEvent("USER-001", inputItems);

        processor.handlePersistOrderItemEvent(event);

        ArgumentCaptor<List<OrderItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderService, times(1)).saveOrderItems(captor.capture());

        List<OrderItem> savedItems = captor.getValue();
        assertEquals(2, savedItems.size());
        assertTrue(savedItems.stream().allMatch(item -> item.userId().equals("USER-001")));
        assertTrue(savedItems.stream().allMatch(item -> item.status() == OrderStatus.PENDING));
        assertTrue(savedItems.stream().map(OrderItem::orderItemId).allMatch(id -> id != null && !id.isEmpty()));
    }

    @Test
    void handlePersistOrderItemEvent_throwsException_whenServiceFails() {
        OrderItem inputItem = new OrderItem(null, "PROD-001", 1, null,
                OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());

        PersistOrderItemEvent event = new PersistOrderItemEvent("USER-001", List.of(inputItem));

        doThrow(new RuntimeException("DB error")).when(orderService).saveOrderItems(anyList());

        assertThrows(RuntimeException.class,
                () -> processor.handlePersistOrderItemEvent(event));

        verify(orderService, times(1)).saveOrderItems(anyList());
    }
}