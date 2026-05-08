package com.order.management.notification.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.order.management.KafkaConsumerBase;
import com.order.management.common.model.NotificationMessage;
import com.order.management.notification.event.PublishNotificationEvent;

@Component
public class NotificationConsumer extends KafkaConsumerBase<NotificationMessage> implements ApplicationRunner {
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired
    public NotificationConsumer(ApplicationEventPublisher eventPublisher) {
        super("notification", "notification-group", NotificationMessage[].class);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        super.init();
    }

    protected void process(NotificationMessage[] data) {
        for (com.order.management.common.model.NotificationMessage message : data) {
            eventPublisher.publishEvent(new PublishNotificationEvent(message));
        }
    }
}   
