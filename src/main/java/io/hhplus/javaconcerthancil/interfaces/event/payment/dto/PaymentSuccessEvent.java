package io.hhplus.javaconcerthancil.interfaces.event.payment.dto;

public record PaymentSuccessEvent(Long orderKey, Long outboxId, String message) {
}
