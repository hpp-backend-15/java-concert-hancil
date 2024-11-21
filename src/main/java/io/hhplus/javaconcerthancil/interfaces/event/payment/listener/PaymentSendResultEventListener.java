package io.hhplus.javaconcerthancil.interfaces.event.payment.listener;

import io.hhplus.javaconcerthancil.domain.outbox.MessageOutbox;
import io.hhplus.javaconcerthancil.domain.outbox.MessageOutboxReader;
import io.hhplus.javaconcerthancil.domain.outbox.MessageOutboxWriter;
import io.hhplus.javaconcerthancil.domain.payments.PaymentPublisher;
import io.hhplus.javaconcerthancil.interfaces.event.payment.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSendResultEventListener {

    private final PaymentPublisher paymentPublisher;
    private final MessageOutboxReader messageOutboxReader;
    private final MessageOutboxWriter messageOutboxWriter;

//    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
        MessageOutbox messageOutbox = messageOutboxReader.findById(event.outboxId());
        log.info("Payment success event received, {}", event);
        log.info("messageOutbox: {}", messageOutbox);
        try{
            messageOutbox.sendSuccess();
            // TODO: messageOutBox의 전송 성공을 저장하지 못하고 있습니다... 이유가 뭘까요
            messageOutboxWriter.save(messageOutbox);
            paymentPublisher.publishEvent(
                    event,
                    System.currentTimeMillis()
            );

        }catch (Exception e){
            log.error("Failed to send payment event to kafka", e);
            messageOutbox.sendFail(e.getMessage());
            messageOutboxWriter.save(messageOutbox);
            throw e;
        }

    }

}
