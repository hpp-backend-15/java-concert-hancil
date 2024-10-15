package io.hhplus.javaconcerthancil.web.reservation;

import java.util.List;

public record ReservationRequest(
        Long userId,
        Long concertId,
        Long scheduleId,
        List<Integer> seatIds
) {
}
