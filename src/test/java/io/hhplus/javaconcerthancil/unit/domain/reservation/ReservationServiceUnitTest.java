package io.hhplus.javaconcerthancil.unit.domain.reservation;

import io.hhplus.javaconcerthancil.domain.concert.SeatRepository;
import io.hhplus.javaconcerthancil.domain.reservation.Reservation;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationItemRepository;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationRepository;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationService;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class ReservationServiceUnitTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationItemRepository reservationItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ReservationService reservationService;  // 테스트할 서비스 클래스

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    void name() {
        //given
        Long userId = 1L;
        Long concertId = 1L;
        Long scheduleId = 1L;
        List<Long> seatIds = List.of(1L, 2L);

        Reservation reservation1 = new Reservation();

        given(reservationService.reserveConcert(userId, concertId, scheduleId, seatIds))
                .willReturn(any(Reservation.class));



        Reservation reservation = reservationService.reserveConcert(
                userId, concertId, scheduleId, seatIds
        );

    }
}
