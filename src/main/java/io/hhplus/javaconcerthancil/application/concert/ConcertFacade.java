package io.hhplus.javaconcerthancil.application.concert;

import io.hhplus.javaconcerthancil.domain.concert.Concert;
import io.hhplus.javaconcerthancil.domain.concert.ConcertService;
import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.interfaces.api.v1.concert.response.GetConcertSchedulesResponse;
import io.hhplus.javaconcerthancil.interfaces.api.v1.concert.response.GetConcertSeatsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ConcertFacade {

    private final ConcertService concertService;

    public GetConcertSchedulesResponse getConcertSchedules(Long concertId) {
        Concert scheduledConcert = concertService.getScheduledConcert(concertId);
        return new GetConcertSchedulesResponse(
                scheduledConcert.getId(),
                scheduledConcert.getSchedules()
        );
    }

    public GetConcertSeatsRequest getConcertSeats(Long concertId, Long scheduleId) {

        List<Seat> concertSeats = concertService.getConcertSeats(concertId, scheduleId);

        return new GetConcertSeatsRequest(concertId, scheduleId, concertSeats);
    }
}
