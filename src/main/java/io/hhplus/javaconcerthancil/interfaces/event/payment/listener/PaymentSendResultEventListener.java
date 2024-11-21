package io.hhplus.javaconcerthancil.interfaces.event.payment.listener;

import io.hhplus.javaconcerthancil.domain.payments.PaymentPublisher;
import io.hhplus.javaconcerthancil.infrastructure.kafka.dto.PaymentMessageForPublish;
import io.hhplus.javaconcerthancil.interfaces.event.payment.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSendResultEventListener {

    private final PaymentPublisher paymentPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
        log.info("Payment success event received, {}", event);
        paymentPublisher.publishEvent(
                event,
                System.currentTimeMillis()
        );
    }

}
