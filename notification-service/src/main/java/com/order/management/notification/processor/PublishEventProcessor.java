package com.order.management.notification.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.order.management.notification.event.PublishNotificationEvent;
import com.order.management.notification.service.NotificationService;

@Component
public class PublishEventProcessor {
    @Autowired
    private NotificationService notificationService;

    @EventListener
    public void process(PublishNotificationEvent event) {
        notificationService.sendNotification(event.notificationMessage());
    }
}
