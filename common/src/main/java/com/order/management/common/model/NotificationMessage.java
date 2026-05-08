package com.order.management.common.model;

import java.time.LocalDateTime;

public record NotificationMessage(String orderItemId, String eventId, LocalDateTime timestamp, String toAddress) {
    
}
