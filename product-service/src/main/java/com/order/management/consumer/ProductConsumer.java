package com.order.management.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.order.management.KafkaConsumerBase;
import com.order.management.model.OrderItem;
import com.order.management.model.OrderStatus;
import com.order.management.service.ProductService;

@Component
public class ProductConsumer extends KafkaConsumerBase<OrderItem> implements ApplicationRunner {
    private final ProductService productService;

    @Autowired
    public ProductConsumer(ProductService productService, ApplicationEventPublisher eventPublisher) {
        super("orders", "product-group", OrderItem[].class);
        this.productService = productService;
    }

    @Override
    protected void process(OrderItem[] data) {
        for (OrderItem item : data) {
            if (item.status() == OrderStatus.PENDING) {
                productService.updateProductStock(item);
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        super.init();
    }
}
