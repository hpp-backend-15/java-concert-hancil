package io.hhplus.javaconcerthancil.support.testcontainer.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KafkaTest {

    private static final String KAFKA_IMAGE = "confluentinc/cp-kafka:7.4.0";
    private static KafkaContainer kafkaContainer;
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;

    @BeforeAll
    public static void setUp() {
        // Kafka 컨테이너 시작
        kafkaContainer = new KafkaContainer(DockerImageName.parse(KAFKA_IMAGE));
        kafkaContainer.start();

        // Producer 설정
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(producerProps);

        // Consumer 설정
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(consumerProps);
    }

    @AfterAll
    public static void tearDown() {
        // Kafka 컨테이너 종료
        if (kafkaContainer != null) {
            kafkaContainer.stop();
        }
    }

    @Test
    @DisplayName("카프카테스트_토픽생성---메세지발행---소비 ")
    public void testKafkaPublishAndConsume() {
        String topic = "test-topic";
        String key = "test-key";
        String value = "test-value";

        // 컨슈머는 토픽을 구독한다.
        consumer.subscribe(Collections.singletonList(topic));

        // 프러듀서는 토픽에 메세지를 전송한다.
        producer.send(new ProducerRecord<>(topic, key, value));
        producer.flush();

        // 컨슈머는 폴링을 통해 메세지를 소비한다.
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
        assertEquals(1, records.count());
        records.forEach(record -> {
            assertEquals(key, record.key());
            assertEquals(value, record.value());
        });
    }
}
