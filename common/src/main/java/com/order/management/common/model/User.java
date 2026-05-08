package com.order.management.common.model;

import java.time.LocalDateTime;

public record User(String id, String name, String email, LocalDateTime createdAt, LocalDateTime lastUpdatedAt) {
}
