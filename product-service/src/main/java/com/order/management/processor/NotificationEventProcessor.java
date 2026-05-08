package com.order.management.processor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.order.management.common.model.NotificationMessage;
import com.order.management.common.model.User;
import com.order.management.KafkaPublisher;
import com.order.management.events.NotificationEvent;

import order.management.reference.service.PolledCache;


@Component
public class NotificationEventProcessor {
    private final KafkaPublisher kafkaPublisher;
    private final PolledCache<String, User> userCache;

    @Autowired
    public NotificationEventProcessor(KafkaPublisher kafkaPublisher, PolledCache<String, User> userCache) {
        this.kafkaPublisher = kafkaPublisher;
        this.userCache = userCache;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        User user = userCache.get(event.userId());
        kafkaPublisher.publish("notification",
                new NotificationMessage[] { new NotificationMessage(event.orderItemId(), UUID.randomUUID().toString(),
                        LocalDateTime.now(), user != null ? user.email() : null) });
    }
}
