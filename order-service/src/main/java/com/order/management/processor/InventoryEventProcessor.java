import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.order.management.KafkaPublisher;
import com.order.management.event.InventoryEvent;
import com.order.management.service.OrderService;

@Component
public class InventoryEventProcessor {
    private final KafkaPublisher kafkaPublisher;

    @Autowired
    public InventoryEventProcessor(KafkaPublisher kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInventoryEvent(InventoryEvent event) {
        kafkaPublisher.publish("orders", event.orderItems());
    }
}