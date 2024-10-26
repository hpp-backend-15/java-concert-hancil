package io.hhplus.javaconcerthancil.integration.domain.concert;

import io.hhplus.javaconcerthancil.domain.concert.*;
import io.hhplus.javaconcerthancil.support.DummyDataLoaderService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ConcertServiceIntegrationTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;
    ;

    @BeforeEach
    @Transactional
    void setUp() {
        Concert concert = new Concert(1L, "Crush", "Crush");

        ConcertSchedule concertSchedule1 = new ConcertSchedule(
                LocalDateTime.of(2024,10,1,10,0),
                LocalDateTime.of(2024,12,23,20,0)
        );
        ConcertSchedule concertSchedule2 = new ConcertSchedule(
                LocalDateTime.of(2024,10,1,10,0),
                LocalDateTime.of(2024,12,23,20,0)
        );
        ConcertSchedule concertSchedule3 = new ConcertSchedule(
                LocalDateTime.of(2024,10,1,10,0),
                LocalDateTime.of(2024,12,24,20,0)
        );

        concert.addSchedule(concertSchedule1);
        concert.addSchedule(concertSchedule2);
        concert.addSchedule(concertSchedule3);
        concertRepository.save(concert);
    }

    @Test
    void concert() {
        assertThat(concertScheduleRepository.count()).isEqualTo(3) ;
    }

    @Test
    @DisplayName("예약가능한 날짜 콘서트 조회")
    void getScheduledConcertTest() {
        //given
        Long concertId = 1L;

        //when
        Concert scheduledConcert = concertService.getScheduledConcert(concertId);

        //then
        assertNotNull(scheduledConcert);
        assertThat(scheduledConcert.getSchedules().size()).isEqualTo(3);

        //예약 가능한 날짜
        assertThat(scheduledConcert.getSchedules().get(0).getReservationAvailableAt())
                .isEqualTo(LocalDateTime.of(2024, 10, 1, 10, 0));
        assertThat(scheduledConcert.getSchedules().get(1).getReservationAvailableAt())
                .isEqualTo(LocalDateTime.of(2024, 10, 1, 10, 0));

        //콘서트 시작 날짜
        assertThat(scheduledConcert.getSchedules().get(0).getConcertAt())
                .isEqualTo(LocalDateTime.of(2024, 12, 24, 19, 0));
        assertThat(scheduledConcert.getSchedules().get(1).getConcertAt())
                .isEqualTo(LocalDateTime.of(2024, 12, 25, 19, 0));

    }


    @Test
    @DisplayName("해당 날짜의 좌석 조회-근데 이제 엉뚱한 concertId를 곁들일 때")
    void getConcertSeatsTest1() {
        //given
        Long concertId = -999L;
        Long scheduleId = 1L;

        //when - then
        assertThrows(IllegalArgumentException.class, () -> concertService.getConcertSeats(concertId, scheduleId));
    }

    @Test
    @DisplayName("해당 날짜의 좌석 조회-근데 이제 엉뚱한 scheduleId를 곁들일 때")
    void getConcertSeatsTest2() {
        //given
        Long concertId = 1L;
        Long scheduleId = -999L;

        //when - then
        assertThrows(IllegalArgumentException.class, () -> concertService.getConcertSeats(concertId, scheduleId));
    }

    @Test
    @DisplayName("해당 날짜의 좌석 조회-좌석정보를 달라")
    void getConcertSeatsTest() {
        final int MAX_SEAT_50 = 50;

        //given
        Long concertId = 1L;
        Long scheduleId = 2L;

        //when
        List<Seat> concertSeats = concertService.getConcertSeats(concertId, scheduleId);

        //then
        assertNotNull(concertSeats);
        assertThat(concertSeats.size()).isEqualTo(MAX_SEAT_50);
        assertThat(concertSeats.get(0).getSeatPrice()).isPositive();
        assertThat(concertSeats.get(0).getStatus()).isInstanceOf(SeatStatus.class);
        //모든 콘서트의 좌석 번호를 양의 정수로 구성한다고 가정했을 때
        assertThat(concertSeats.get(0).getSeatNumber()).isLessThanOrEqualTo(MAX_SEAT_50);
    }


}
