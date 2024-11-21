package io.hhplus.javaconcerthancil.interfaces.event.payment.dto;

public record PaymentSuccessEvent(Long orderKey, String message) {
}
