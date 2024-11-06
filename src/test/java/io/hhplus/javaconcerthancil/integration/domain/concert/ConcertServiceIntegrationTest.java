package io.hhplus.javaconcerthancil.integration.domain.concert;

import io.hhplus.javaconcerthancil.domain.concert.*;
import io.hhplus.javaconcerthancil.infrastructure.persistence.ConcertJpaRepository;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConcertServiceIntegrationTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    final int CONCERT_SCHEDULED_SIZE_3 = 3;
    final int MAX_SEAT_50 = 50;

    @BeforeAll
    @Transactional
    void setUp() {
        Concert concert = new Concert("Crush콘서트", "Crush_크리스마스_공연");

        ConcertSchedule concertSchedule1 = new ConcertSchedule(
                LocalDateTime.of(2024,10,1,10,0),
                LocalDateTime.of(2024,12,23,20,0)
        );
        ConcertSchedule concertSchedule2 = new ConcertSchedule(
                LocalDateTime.of(2024,10,1,10,0),
                LocalDateTime.of(2024,12,24,20,0)
        );
        ConcertSchedule concertSchedule3 = new ConcertSchedule(
                LocalDateTime.of(2024,10,1,10,0),
                LocalDateTime.of(2024,12,25,20,0)
        );

        concert.addSchedule(concertSchedule1);
        concert.addSchedule(concertSchedule2);
        concert.addSchedule(concertSchedule3);
        concertJpaRepository.save(concert);

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
        assertThat(scheduledConcert.getSchedules().size()).isEqualTo(CONCERT_SCHEDULED_SIZE_3);


        /**
         * 예약 가능날짜는 콘서트 시작 일정과 관계없이 고정되어있고
         * 시작일정은 연속적으로열린다는 가정
         */
        //예약 가능한 날짜
        for (int i = 0; i < scheduledConcert.getSchedules().size(); i++) {
            assertThat(scheduledConcert.getSchedules().get(i).getReservationAvailableAt())
                    .isEqualTo(LocalDateTime.of(2024, 10, 1, 10, 0));
        //콘서트 시작 날짜
            assertThat(scheduledConcert.getSchedules().get(i).getConcertAt())
                    .isEqualTo(LocalDateTime.of(2024, 12, 23+i, 20, 0));
        }
    }


    @Test
    @DisplayName("해당 날짜의 좌석 조회-근데 이제 엉뚱한 concertId를 곁들일 때")
    void getConcertSeatsTest1() {
        //given
        Long concertId = -999L;
        Long scheduleId = 1L;

        //when - then
        assertThrows(ApiException.class, () -> concertService.getConcertSeats(concertId, scheduleId));
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

        //given
        Long concertId = 1L;
        Long scheduleId = 2L;

        //when
        List<Seat> concertSeats = concertService.getConcertSeats(concertId, scheduleId);

        //then
        assertNotNull(concertSeats);

        assertThat(concertSeats.size()).isEqualTo(MAX_SEAT_50);

        for(int i=0;i<concertSeats.size();i++){
            //모든 콘서트의 좌석 번호를 양의 정수로 구성한다고 가정했을 때
            assertThat(concertSeats.get(i).getSeatNumber()).isLessThanOrEqualTo(MAX_SEAT_50);
            assertThat(concertSeats.get(i).getStatus()).isInstanceOf(SeatStatus.class);

            int expectedPrice = (i >= 40) ? 15_000 : 10_000; // 가격 결정
            assertThat(concertSeats.get(i).getSeatPrice()).isEqualTo(expectedPrice);
        }


    }


}
