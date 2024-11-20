package io.hhplus.javaconcerthancil.integration.infrastructure.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class KafkaProducerTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void 카프카_전송_테스트() {
        try {
            ProducerDTO producerDto = new ProducerDTO("ConcertPayment","1", "test message");

            Message<ProducerDTO> message = MessageBuilder
                    .withPayload(producerDto)
                    .setHeader(KafkaHeaders.TOPIC, producerDto.getTopic())
                    .setHeader(KafkaHeaders.KEY, producerDto.getKey())
                    .build();

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

            SendResult<String, Object> sendResult = future.get(); // 비동기 결과를 동기적으로 대기
            assertThat(sendResult.getRecordMetadata()).isNotNull();
            System.out.println("Message sent successfully to topic: " + sendResult.getRecordMetadata().topic());


        } catch (Exception e) {
            System.out.printf(">>> [ALARM] Kafka Send Error= {}" + e);
        }
    }

}
