package io.hhplus.javaconcerthancil.web.concert.response;

import io.hhplus.javaconcerthancil.domain.concert.Seat;

import java.util.List;

public record GetConcertSeatsRequest(
        Long concertId,
        Long scheduleId,
        List<Seat> seats
) {
}
