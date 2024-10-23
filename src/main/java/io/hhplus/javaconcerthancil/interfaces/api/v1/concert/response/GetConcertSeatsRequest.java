package io.hhplus.javaconcerthancil.interfaces.api.v1.concert.response;

import io.hhplus.javaconcerthancil.domain.concert.Seat;

import java.util.List;

public record GetConcertSeatsRequest(
        Long concertId,
        Long scheduleId,
        List<Seat> seats
) {
}
