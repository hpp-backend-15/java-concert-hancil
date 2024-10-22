package io.hhplus.javaconcerthancil.interfaces.api.v1.reservation;

import io.hhplus.javaconcerthancil.domain.ReservationStatus;

import java.util.List;

public record ReservationResponse(
        Long reservationId,
        Long concertId,
        Long scheduleId,
        List<Long> seats,
        long totalPrice,
        ReservationStatus status
) {
}
