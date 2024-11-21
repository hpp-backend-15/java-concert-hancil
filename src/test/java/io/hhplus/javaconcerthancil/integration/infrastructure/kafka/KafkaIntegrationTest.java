package io.hhplus.javaconcerthancil.integration.infrastructure.kafka;

import io.hhplus.javaconcerthancil.support.config.KafkaConsumerConfig;
import io.hhplus.javaconcerthancil.support.dto.ProducerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, ProducerDTO> kafkaTestTemplate;
    private KafkaConsumer<String, ProducerDTO> kafkaConsumer;

    private final ProducerDTO producerDto = new ProducerDTO("ConcertPayment","1", "test message");

    @BeforeEach
    void setUp() {
        KafkaConsumerConfig config = new KafkaConsumerConfig();
        this.kafkaConsumer = (KafkaConsumer<String, ProducerDTO>) config.testConsumerFactory().getConsumerFactory().createConsumer();
    }

    @Test
    @Order(1)
    void 카프카_전송_테스트() {
        try {

            Message<ProducerDTO> message = MessageBuilder
                    .withPayload(producerDto)
                    .setHeader(KafkaHeaders.TOPIC, producerDto.getTopic())
                    .setHeader(KafkaHeaders.KEY, producerDto.getKey())
                    .build();

            CompletableFuture<SendResult<String, ProducerDTO>> future = kafkaTestTemplate.send(message);

            SendResult<String, ProducerDTO> sendResult = future.get(); // 비동기 결과를 동기적으로 대기
            assertThat(sendResult.getRecordMetadata()).isNotNull();
            log.info("Message sent successfully to topic: " + sendResult.getRecordMetadata().topic());
        } catch (Exception e) {
//            log.error(">>> [ALARM] Kafka Send Error= {}" , e.getMessage());
            fail(e.getMessage());
        }
    }


    @Test
    @Order(2)
    //역직렬화 실패.. 해결중
    void 카프카_소비_테스트() {
        try {

            kafkaConsumer.subscribe(Collections.singletonList(producerDto.getTopic()));

            ConsumerRecords<String, ProducerDTO> records = kafkaConsumer.poll(Duration.ofSeconds(60)); // 메시지 수신
            log.info("record count: {}", records.count());
            assertThat(records.count()).isGreaterThan(0); // 메시지가 1개 이상인지 확인
            for (ConsumerRecord<String, ProducerDTO> record : records) {
                log.info("Consumed message: key={}, value={}, topic={}}",
                        record.key(), record.value(), record.topic());
                log.info("record value: {}", record.value().getMessage());
                log.info("dto value: {}", producerDto.getMessage());
                assertThat(record.value().getMessage()).isEqualTo(producerDto.getMessage()); // 값 검증
            }

        } catch (Exception e) {
//            log.error(">>> [ALARM] Kafka Send Error= {}", e.getMessage());
            fail(e.getMessage());
        }
    }


}
