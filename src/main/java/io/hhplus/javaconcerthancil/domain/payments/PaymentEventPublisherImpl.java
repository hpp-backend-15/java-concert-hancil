package io.hhplus.javaconcerthancil.domain.payments;

import io.hhplus.javaconcerthancil.interfaces.external.payments.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishPaymentResult(PaymentSuccessEvent paymentSuccessEvent) {
        applicationEventPublisher.publishEvent(paymentSuccessEvent);
    }
}
