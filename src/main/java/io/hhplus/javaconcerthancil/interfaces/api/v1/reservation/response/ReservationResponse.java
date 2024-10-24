package io.hhplus.javaconcerthancil.interfaces.api.v1.reservation.response;

import io.hhplus.javaconcerthancil.domain.reservation.ReservationStatus;

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
