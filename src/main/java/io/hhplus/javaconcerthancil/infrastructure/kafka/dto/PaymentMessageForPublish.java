package io.hhplus.javaconcerthancil.infrastructure.kafka.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentMessageForPublish(
        Long id,
        Long userId,
        String concertTitle,
        LocalDate concertOpenDate,
        LocalDateTime concertStartAt,
        LocalDateTime concertEndAt,
        Integer seatAmount,
        Integer seatPosition,
        LocalDateTime reservedAt,
        LocalDateTime paymentDate
) {
}
