package io.hhplus.javaconcerthancil.interfaces.external.payments.dto;

public record PaymentSuccessEvent(Long orderKey, String message) {
}
