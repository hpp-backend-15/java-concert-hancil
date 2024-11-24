package io.hhplus.javaconcerthancil.support.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.javaconcerthancil.infrastructure.kafka.dto.KafkaMessage;
import io.hhplus.javaconcerthancil.infrastructure.kafka.dto.PaymentMessageForPublish;
import io.hhplus.javaconcerthancil.support.dto.ProducerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    private final String LOCAL_BOOTSTRAP_SERVER = "localhost:9090";
    private final String PAYMENT_GROUP = "payment_group";
    private final Integer paymentCnt = 1;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> paymentConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(LOCAL_BOOTSTRAP_SERVER, PAYMENT_GROUP, KafkaMessage.class));
        factory.setConcurrency(paymentCnt); /// consumer 를 처리하는 Thread 개수로 Partition에 할당 됨.
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE); // 메시지를 수신하자마자 ACK(acknowledge)를 처리
        factory.getContainerProperties().setPollTimeout(10000);

        log.info("카프카 결제 컨슈머 그룹 생성 완료 : {}", PAYMENT_GROUP);
        return factory;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProducerDTO> testConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProducerDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(LOCAL_BOOTSTRAP_SERVER, PAYMENT_GROUP, ProducerDTO.class));
        factory.setConcurrency(paymentCnt); /// consumer 를 처리하는 Thread 개수로 Partition에 할당 됨.
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE); // 메시지를 수신하자마자 ACK(acknowledge)를 처리
        factory.getContainerProperties().setPollTimeout(10000);

        log.info("카프카 컨슈머 그룹 생성 완료 : {}", PAYMENT_GROUP);
        return factory;
    }

    private ConsumerFactory consumerFactory(String bootstrapAddress, String groupId, Class clazz) {
        Map<String, Object> props = consumerConfig(bootstrapAddress, groupId);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(clazz, false));
    }

    private ConsumerFactory consumerFactory(String bootstrapAddress, String groupId, Class clazz, Class payloadClazz) {
        Map<String, Object> props = consumerConfig(bootstrapAddress, groupId);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(clazz, payloadClazz);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<KafkaMessage>(type, objectMapper, false));
    }

    private Map<String, Object> consumerConfig(String bootstrapAddress, String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024 * 1024);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 2000);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // , earliest, latest
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 자동 오프셋 커밋을 비활성화

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }


}
