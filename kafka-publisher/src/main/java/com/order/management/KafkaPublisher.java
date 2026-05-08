package com.order.management;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class KafkaPublisher {
    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaPublisher() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<String, String>(props);
    }

    public <T> void publish(String topic, T entity) {
        String jsonValue = objectMapper.writeValueAsString(entity);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonValue);

        this.producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.println("Sent to: " + metadata.topic() + " Partition: " + metadata.partition());
            } else {
                exception.printStackTrace();
            }
        });
    }
}