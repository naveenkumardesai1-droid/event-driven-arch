package com.order.management;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import tools.jackson.databind.ObjectMapper;

public abstract class KafkaConsumerBase<T> {
    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private final ExecutorService executor;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T[]> dataType;

    public KafkaConsumerBase(String topic, String groupId, Class<T[]> dataType) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        this.topic = topic;
        this.dataType = dataType;
        this.executor = Executors.newSingleThreadExecutor();
        this.consumer = new KafkaConsumer<>(props);
    }

    protected abstract void process(T[] data);

    public void init() {
        executor.submit(() -> {
            try {
                consumer.subscribe(Collections.singletonList(topic));
                while (running.get()) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
                    records.forEach(record -> {
                        try {
                            T[] data = objectMapper.readValue(record.value(), dataType);
                            process(data);
                        } catch (Exception e) {
                            System.err.println("Error deserializing message: " + e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Consumer error: " + e.getMessage());
            } finally {
                consumer.close();
            }
        });
    }

    public void stop() {
        running.set(false);
        consumer.wakeup();
        executor.shutdown();
    }

    public void run(String... args) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
}
