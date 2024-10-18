package io.hhplus.javaconcerthancil.web.reservation;

import io.hhplus.javaconcerthancil.domain.ReservationStatus;

import java.util.HashMap;
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
