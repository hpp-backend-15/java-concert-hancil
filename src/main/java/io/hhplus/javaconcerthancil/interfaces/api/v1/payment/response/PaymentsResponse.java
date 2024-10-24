package io.hhplus.javaconcerthancil.interfaces.api.v1.payment.response;

import io.hhplus.javaconcerthancil.domain.payments.PaymentStatus;

public record PaymentsResponse(
        long paymentId,
        long amount,
        PaymentStatus status

) {
}
