package io.hhplus.javaconcerthancil.interfaces.api.v1.reservation.request;

import java.util.List;

public record ReservationRequest(
        Long userId,
        Long concertId,
        Long scheduleId,
        List<Long> seatIds
) {
}
