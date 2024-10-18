package io.hhplus.javaconcerthancil.domain.reservation;

import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.domain.concert.SeatRepository;
import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationItemRepository reservationItemRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    public Reservation reserveConcert(Long userId, Long concertId, Long scheduleId, List<Long> seatIds) {

        // 1. 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reservation reservation = new Reservation(user);
        Reservation savedResrvation = reservationRepository.save(reservation);// 예약 먼저 저장

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found"));

            if (!seat.getStatus().equals(SeatStatus.AVAILABLE)) {
                throw new RuntimeException("Seat not available");
            }

            // 좌석 상태 변경
            seat.setStatus(SeatStatus.RESERVED);
            seatRepository.save(seat);

            // 예약 항목 저장
            ReservationItem reservationItem = new ReservationItem();
            reservationItem.setReservation(savedResrvation);
            reservationItem.setSeat(seat);
            reservationItem.setSeatPrice(seat.getSeatPrice());

            reservationItemRepository.save(reservationItem);
        }

        return savedResrvation;
    }
}
