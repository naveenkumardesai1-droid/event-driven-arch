package com.order.management.notification.event;

import com.order.management.common.model.NotificationMessage;

public record PublishNotificationEvent(NotificationMessage notificationMessage) {
}
