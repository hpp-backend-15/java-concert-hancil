package io.hhplus.javaconcerthancil.interfaces.api.v1.payment;

public record PaymentsResponse(
        long paymentId,
        int amount,
        String status

) {
}
