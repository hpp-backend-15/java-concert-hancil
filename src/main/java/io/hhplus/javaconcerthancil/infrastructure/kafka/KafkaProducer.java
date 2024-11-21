package io.hhplus.javaconcerthancil.infrastructure.kafka;

import io.hhplus.javaconcerthancil.infrastructure.kafka.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    private final static  String PAYMENT_TOPIC = "ConcertPayment";

    public void producePayment(KafkaMessage kafkaMessage) {
        try {
            Message<KafkaMessage> message = MessageBuilder
                    .withPayload(kafkaMessage)
                    .setHeader(KafkaHeaders.TOPIC, PAYMENT_TOPIC)
                    .setHeader(KafkaHeaders.KEY, kafkaMessage.getEventKey())
                    .build();

            kafkaTemplate.send(message);
        } catch (Exception e) {
            log.error(">>> [ALARM] Kafka producePayment Send Error= {}" + e);
            throw e;
        }
    }

    public void produceEvent(KafkaMessage kafkaMessage) {
        try {
            Message<KafkaMessage> message = MessageBuilder
                    .withPayload(kafkaMessage)
                    .setHeader(KafkaHeaders.TOPIC, PAYMENT_TOPIC)
                    .setHeader(KafkaHeaders.KEY, kafkaMessage.getEventKey())
                    .build();
            kafkaTemplate.send(message);
        } catch (Exception e) {
            log.error(">>> [ALARM] Kafka producePayment Send Error= {}" + e);
            throw e;
        }


    }
}
