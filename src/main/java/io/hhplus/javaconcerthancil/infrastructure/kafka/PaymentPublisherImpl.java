package io.hhplus.javaconcerthancil.infrastructure.kafka;

import io.hhplus.javaconcerthancil.domain.payments.PaymentPublisher;
import io.hhplus.javaconcerthancil.infrastructure.kafka.dto.KafkaMessage;
import io.hhplus.javaconcerthancil.infrastructure.kafka.dto.PaymentMessageForPublish;
import io.hhplus.javaconcerthancil.interfaces.event.payment.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentPublisherImpl implements PaymentPublisher {

    private final KafkaProducer kafkaProducer;

    @Override
    public void publishEvent(PaymentSuccessEvent event, Long publishTimestamp) {
        kafkaProducer.produceEvent(
                new KafkaMessage(
                        publishTimestamp,
                        System.currentTimeMillis(),
                        event
                )
        );
    }

    @Override
    public void publishPayment(PaymentMessageForPublish message, Long publishTimestamp) {
        kafkaProducer.producePayment(
                new KafkaMessage(
                        publishTimestamp,
                        System.currentTimeMillis(),
                        message
                )
        );
    }
}
