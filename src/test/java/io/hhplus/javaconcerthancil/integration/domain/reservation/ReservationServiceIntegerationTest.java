package io.hhplus.javaconcerthancil.integration.domain.reservation;

import io.hhplus.javaconcerthancil.domain.concert.*;
import io.hhplus.javaconcerthancil.domain.payments.Payment;
import io.hhplus.javaconcerthancil.domain.payments.PaymentStatus;
import io.hhplus.javaconcerthancil.domain.reservation.*;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.infrastructure.persistence.concert.ConcertJpaRepository;
import io.hhplus.javaconcerthancil.infrastructure.persistence.payment.PaymentJpaRepository;
import io.hhplus.javaconcerthancil.infrastructure.persistence.reservation.ReservationItemJpaRepository;
import io.hhplus.javaconcerthancil.infrastructure.persistence.reservation.ReservationJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationServiceIntegerationTest {

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationJpaRepository reservationRepository;

    @Autowired
    private ReservationItemJpaRepository reservationItemRepository;

    @Autowired
    private PaymentJpaRepository paymentRepository;


    @BeforeAll
    @Transactional
    void setUp() {
        userRepository.save(new User("JHC"));

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
    @Transactional
    void createReservation() {

        //given
        List<Long> seatIds = List.of(1L, 2L, 3L);


        //좌석데이터가 정상적으로 생성되었는지 확인
        List<Seat> seats  = seatRepository.findAllByIdForUpdate(seatIds);
        for(Seat seat: seats){
            assertTrue(seat.isAvailable());
        }

        Long userId = 1L;
        User optionalUser = userRepository.findById(userId);
        // 사용자데이터가 정상적으로 생성되었는지 확인
        assertNotNull(optionalUser);
        assertThat(optionalUser.getId()).isEqualTo(userId);

        //
        Reservation reservation = reservationRepository.save(new Reservation(optionalUser));
        assertThat(reservation.getUser().getId()).isEqualTo(userId);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId);

            if (!seat.isAvailable()) {
                throw new RuntimeException("Seat is not available");
            }

            // 좌석 상태를 'RESERVED'로 변경
            seat.setStatus(SeatStatus.RESERVED);
            seatRepository.save(seat); // 업데이트된 좌석 저장

            // 예약 아이템 생성 및 예약에 추가
            ReservationItem item = new ReservationItem();
            item.setSeat(seat);
            item.setSeatPrice(seat.getSeatPrice()); // 좌석 가격 설정
            reservation.addItem(item); // 예약에 아이템 추가
        }

        // 예약 저장 및 예비결제 저장
        Reservation savedReservation = reservationRepository.save(reservation);
        paymentRepository.save(new Payment(savedReservation));

        //then
        Reservation createdReservation = reservationRepository.findById(reservation.getId()).get();
        List<ReservationItem> reservationItems = reservationItemRepository.findAll();

        //reservation의 데이터는 의도대로 생성되었는가?
        assertThat(createdReservation.getUser().getId()).isEqualTo(userId);
        assertThat(createdReservation.getStatus()).isEqualTo(ReservationStatus.PENDING);

        //reservation_item의 데이터는 의도대로 생성되었는가?
        assertThat(reservationItems.size()).isEqualTo(seatIds.size());
        for(ReservationItem reservationItem: reservationItems){
            assertThat(reservationItem.getReservation().getId()).isEqualTo(createdReservation.getId());
            assertTrue(seatIds.contains(reservationItem.getSeat().getId()));
        }

        List<Payment> payments = paymentRepository.findAll();
        for(Payment payment: payments){
            assertThat(payment.getReservation().getId()).isEqualTo(createdReservation.getId());
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        }

    }
}
