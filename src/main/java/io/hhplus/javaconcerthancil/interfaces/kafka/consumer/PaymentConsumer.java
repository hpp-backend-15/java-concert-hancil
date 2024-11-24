package io.hhplus.javaconcerthancil.interfaces.kafka.consumer;

import io.hhplus.javaconcerthancil.infrastructure.kafka.dto.KafkaMessage;
import io.hhplus.javaconcerthancil.infrastructure.kafka.dto.PaymentMessageForPublish;
import io.hhplus.javaconcerthancil.interfaces.event.payment.dto.PaymentSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class PaymentConsumer {

    private static CountDownLatch latch = new CountDownLatch(1);
//    private static PaymentSuccessEvent paymentMessage;

    @KafkaListener(topics = "ConcertPayment", containerFactory = "paymentConsumerFactory")
    public void consume(KafkaMessage<PaymentSuccessEvent> kafkaTemplate, Acknowledgment ack) {
        log.info("kafkaPaymentConsumer 수신한 데이터 : {}", kafkaTemplate.toString());
        try {
//            paymentMessage = kafkaTemplate.getPayload();
            ack.acknowledge();
        } catch (Exception e) {
            log.error("consume Error - Exception : {}", e.getMessage());
            throw e;
        }
        latch.countDown();
    }

//    public PaymentSuccessEvent getPaymentMessage() {
//        return paymentMessage;
//    }
}
