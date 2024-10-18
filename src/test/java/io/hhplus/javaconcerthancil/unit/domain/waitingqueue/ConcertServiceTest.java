package io.hhplus.javaconcerthancil.unit.domain.waitingqueue;

import io.hhplus.javaconcerthancil.domain.concert.Concert;
import io.hhplus.javaconcerthancil.domain.concert.ConcertService;
import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
import io.hhplus.javaconcerthancil.support.DummyDataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ConcertServiceTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private DummyDataLoaderService dummyDataLoaderService;

    @BeforeEach
    void setUp() {
        dummyDataLoaderService.loadDummyData();
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
        assertThat(scheduledConcert.getSchedules().size()).isEqualTo(2);

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
        assertThat(concertSeats.get(0).getSeatNumber()).isPositive();
    }


}
