package com.order.management.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.order.management.KafkaPublisher;

@Configuration
public class KafkaProducerConfig {
    @Bean
    public KafkaPublisher kafkaPublisher() {
        return new KafkaPublisher();
    }
}