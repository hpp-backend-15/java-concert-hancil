package io.hhplus.javaconcerthancil.web.concert;

import io.hhplus.javaconcerthancil.domain.concert.Concert;
import io.hhplus.javaconcerthancil.domain.concert.ConcertSchedule;
import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
import io.hhplus.javaconcerthancil.web.concert.response.GetConcertSchedulesResponse;
import io.hhplus.javaconcerthancil.web.concert.response.GetConcertSeatsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "콘서트 API", description = "콘서트 예약, 좌석 조회 및 결제")
@RestController
@RequestMapping("/v1/concert")
@Log4j2
public class ConcertController {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Operation(summary = "콘서트 예약 가능한 날짜 조회 API", description = "콘서트의 예약 가능한 날짜를 조회합니다.")
    @GetMapping("{concertId}/schedules")
    public GetConcertSchedulesResponse getConcertSchedules(
            HttpServletRequest request,
            @PathVariable("concertId") Long concertId
    ) {
        log.info("token: {}", request.getHeader("Authorization"));
        log.info("concertId: {}", concertId);

        List<ConcertSchedule> list = new ArrayList<>();
        Concert concert = new Concert(1L, "Crush", "크러쉬 크리스마스콘서트");
        ConcertSchedule schedule1 = new ConcertSchedule(
                1L, NOW, NOW.plusMonths(2)
        );
        ConcertSchedule schedule2 = new ConcertSchedule(
                2L, NOW, NOW.plusMonths(2)
        );
        list.add(schedule1);
        list.add(schedule2);

        return new GetConcertSchedulesResponse(concertId, list);
    }

    @Operation(summary = "해당 날짜의 좌석 조회 API", description = "주어진 콘서트와 날짜의 예약 가능 좌석 정보를 조회합니다.")
    @GetMapping("{concertId}/schedules/{scheduleId}/seats")
    public GetConcertSeatsRequest getConcertSeats(
            HttpServletRequest request,
            @PathVariable("concertId") Long concertId,
            @PathVariable("scheduleId") Long scheduleId
    ) {
        log.info("token: {}", request.getHeader("Authorization"));
        log.info("concertId: {}", concertId);
        log.info("scheduleId: {}", scheduleId);

        List<Seat> list = new ArrayList<>();
        Seat seat1 = new Seat(1L, 20, SeatStatus.AVAILABLE, 10_000);
        Seat seat2 = new Seat(2L, 21, SeatStatus.OCCUPIED, 10_000);
        Seat seat3 = new Seat(3L, 22, SeatStatus.RESERVED, 15_000);
        list.add(seat1);
        list.add(seat2);
        list.add(seat3);

        return new GetConcertSeatsRequest(concertId, scheduleId, list);
    }


}
