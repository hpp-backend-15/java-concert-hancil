package io.hhplus.javaconcerthancil.unit.domain.concert;

import io.hhplus.javaconcerthancil.domain.concert.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class ConcertServiceUnitTest {

    @Mock
    private ConcertRepository concertRepository;  // 의존성 모킹

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    @InjectMocks
    private ConcertService concertService;  // 테스트할 서비스 클래스

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("예약가능한 날짜 콘서트 조회")
    void getScheduledConcertTest() {
        //given
        Long concertId = 1L;
        Concert concert = new Concert(concertId, "Test Concert", "Test Concert");

        ConcertSchedule schedule1 = new ConcertSchedule(
                1L,
                LocalDateTime.of(2024, 10, 1, 10, 0),
                LocalDateTime.of(2024, 12, 24, 19, 0)
        );

        ConcertSchedule schedule2 = new ConcertSchedule(
                2L,
                LocalDateTime.of(2024, 10, 1, 10, 0),
                LocalDateTime.of(2024, 12, 25, 19, 0)
        );

        concert.setSchedules(Arrays.asList(schedule1, schedule2));

        given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));  // 모킹된 동작

        //when
        Concert scheduledConcert = concertService.getScheduledConcert(concertId);

        //then
        assertNotNull(scheduledConcert);
        assertThat(scheduledConcert.getSchedules().size()).isEqualTo(2);

        // 예약 가능한 날짜 검증
        assertThat(scheduledConcert.getSchedules().get(0).getReservationAvailableAt())
                .isEqualTo(LocalDateTime.of(2024, 10, 1, 10, 0));
        assertThat(scheduledConcert.getSchedules().get(1).getReservationAvailableAt())
                .isEqualTo(LocalDateTime.of(2024, 10, 1, 10, 0));

        // 콘서트 시작 날짜 검증
        assertThat(scheduledConcert.getSchedules().get(0).getConcertAt())
                .isEqualTo(LocalDateTime.of(2024, 12, 24, 19, 0));
        assertThat(scheduledConcert.getSchedules().get(1).getConcertAt())
                .isEqualTo(LocalDateTime.of(2024, 12, 25, 19, 0));
    }

    @Test
    @DisplayName("해당 날짜의 좌석 조회 - 엉뚱한 concertId를 곁들일 때")
    void getConcertSeatsTest1() {
        //given
        Long concertId = -999L;
        Long scheduleId = 1L;

        // 모킹: concertId가 없을 때 예외 발생
        when(concertRepository.findById(concertId)).thenThrow(new IllegalArgumentException("Invalid concertId"));

        //when - then
        assertThrows(IllegalArgumentException.class, () -> concertService.getConcertSeats(concertId, scheduleId));
    }

    @Test
    @DisplayName("해당 날짜의 좌석 조회 - 엉뚱한 scheduleId를 곁들일 때")
    void getConcertSeatsTest2() {
        //given
        Long concertId = 1L;
        Long scheduleId = -999L;

        // 모킹: scheduleId가 없을 때 예외 발생
        Concert concert = new Concert(concertId, "Test Concert", "Test Concert");
        given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));
        given(concertScheduleRepository.findById(concertId)).willThrow(new IllegalArgumentException("ConcertSchedule not found"));

        //when - then
        assertThrows(IllegalArgumentException.class, () -> concertService.getConcertSeats(concertId, scheduleId));
    }

    @Test
    @DisplayName("해당 날짜의 좌석 조회 - 좌석정보를 달라")
    void getConcertSeatsTest() {
        final int MAX_SEAT_50 = 50;

        //given
        Long concertId = 1L;
        Long scheduleId = 2L;

        // 모킹: 좌석 리스트를 반환하도록 설정
        Concert concert = new Concert(concertId, "Test Concert", "Test Concert");
        ConcertSchedule schedule = new ConcertSchedule(
                scheduleId,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );
        Seat seat1 = new Seat(1L, 1, SeatStatus.AVAILABLE, 10_000);
        List<Seat> seats = Arrays.asList(seat1);

        concert.setSchedules(Collections.singletonList(schedule));
        schedule.setSeats(seats);

        given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));
        given(concertScheduleRepository.findById(scheduleId)).willReturn(Optional.of(schedule));

        //when
        List<Seat> concertSeats = concertService.getConcertSeats(concertId, scheduleId);

        //then
        assertNotNull(concertSeats);
        assertThat(concertSeats.size()).isEqualTo(1);
        assertThat(concertSeats.get(0).getSeatPrice()).isPositive();
        assertThat(concertSeats.get(0).getStatus()).isInstanceOf(SeatStatus.class);
        assertThat(concertSeats.get(0).getSeatNumber()).isLessThanOrEqualTo(MAX_SEAT_50);
    }

}
