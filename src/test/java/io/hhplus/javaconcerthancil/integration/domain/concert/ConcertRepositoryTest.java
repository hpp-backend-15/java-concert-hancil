package io.hhplus.javaconcerthancil.integration.domain.concert;

import io.hhplus.javaconcerthancil.domain.concert.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConcertRepositoryTest {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

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

        concertSchedule1.addSeats();
        concertSchedule2.addSeats();
        concertSchedule3.addSeats();
        concert.addSchedule(concertSchedule1);
        concert.addSchedule(concertSchedule2);
        concert.addSchedule(concertSchedule3);
        concertRepository.save(concert);

    }


    @Test
    @DisplayName("Crush 콘서트는 3개가 예정되어 있어")
    @Transactional
    void setConcertScheduleRepositoryTest() {
        assertThat(concertScheduleRepository.count()).isEqualTo(3) ;
    }

    @Test
    @DisplayName("각각 23,24,25일에 열리지")
    @Transactional
    void setConcertScheduleRepositoryTest2() {
        Concert concert = concertRepository.findById(1L).get();
        int size = concert.getSchedules().size();
        for (int i = 0; i < size; i++) {
            assertThat(concert.getSchedules().get(i).getConcertAt())
                    .isEqualTo(LocalDateTime.of(2024,12,23+i,20,0));
        }

    }

    @Test
    @DisplayName("각 스케줄에 50개의 좌석이 생성되어야 해")
    @Transactional
    void testConcertScheduleSeatsCount() {
        Concert concert = concertRepository.findById(1L).get();
        int size = concert.getSchedules().size();
        for (int i = 0; i < size; i++) {
            assertThat(concert.getSchedules().get(i).getSeats().size()).isEqualTo(50);
        }

    }

    @Test
    @DisplayName("모든 좌석의 상태는 기본적으로 AVAILABLE 이어야 해")
    void testSeatsStatus() {
        Concert concert = concertRepository.findById(1L).get();
        int concertScheduleSize = concert.getSchedules().size();

        for (int i = 0; i < concertScheduleSize; i++) {
            for (Seat seat :concert.getSchedules().get(i).getSeats()){
                assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
            }
        }
    }

    @Test
    @DisplayName("마지막 10개의 좌석 가격은 15_000원이어야 해. 그외는 10_000원이야")
    void testLastTenSeatsPrice() {
        Concert concert = concertRepository.findById(1L).get();
        int concertScheduleSize = concert.getSchedules().size();

        for (int i = 0; i < concertScheduleSize; i++) {
            List<Seat> seats = concert.getSchedules().get(i).getSeats();
            for(int j=0; j<50; j++){
                if(j<40){
                    assertThat(seats.get(j).getSeatPrice()).isEqualTo(10_000);
                }else{
                    assertThat(seats.get(j).getSeatPrice()).isEqualTo(15_000);
                }
            }
        }
    }

}
