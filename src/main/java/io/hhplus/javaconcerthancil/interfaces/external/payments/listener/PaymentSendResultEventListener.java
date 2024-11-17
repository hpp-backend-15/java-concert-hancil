package io.hhplus.javaconcerthancil.interfaces.external.payments.listener;

import io.hhplus.javaconcerthancil.interfaces.external.payments.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSendResultEventListener {

    @Async
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
        log.info("Payment success event received, {}", event);
    }

}
