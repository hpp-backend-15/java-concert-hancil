package io.hhplus.javaconcerthancil.domain.concert;

public interface ConcertRepository {
    Concert getConcert(long concertId);
    ConcertSchedule getConcertSeats(Long scheduleId);
}
