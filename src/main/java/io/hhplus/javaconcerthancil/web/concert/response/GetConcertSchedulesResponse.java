package io.hhplus.javaconcerthancil.web.concert.response;

import io.hhplus.javaconcerthancil.domain.concert.ConcertSchedule;

import java.util.List;

public record GetConcertSchedulesResponse(
        Long concertId,
        List<ConcertSchedule> schedules) {
}
