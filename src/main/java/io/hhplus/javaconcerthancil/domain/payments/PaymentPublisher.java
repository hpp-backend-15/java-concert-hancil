package io.hhplus.javaconcerthancil.domain.payments;

import io.hhplus.javaconcerthancil.infrastructure.kafka.dto.PaymentMessageForPublish;
import io.hhplus.javaconcerthancil.interfaces.event.payment.dto.PaymentSuccessEvent;

public interface PaymentPublisher {
    void publishPayment(PaymentMessageForPublish message, Long publishTimestamp);
    void publishEvent(PaymentSuccessEvent event, Long publishTimestamp);
}
