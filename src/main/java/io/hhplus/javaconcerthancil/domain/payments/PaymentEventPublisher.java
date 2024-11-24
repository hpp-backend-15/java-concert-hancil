package io.hhplus.javaconcerthancil.domain.payments;

import io.hhplus.javaconcerthancil.interfaces.event.payment.dto.PaymentSuccessEvent;

public interface PaymentEventPublisher {

    void publishPaymentResult(PaymentSuccessEvent paymentSuccessEvent);

}
