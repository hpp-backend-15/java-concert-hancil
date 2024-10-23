package io.hhplus.javaconcerthancil.domain.reservation;

import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.domain.concert.SeatRepository;
import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationItemRepository reservationItemRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Reservation reserveConcert(Long userId, Long concertId, Long scheduleId, List<Long> seatIds) {

        // 1. 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.E404, LogLevel.INFO, "User not found"));

        // 2. 좌석 유효성 검사
//        List<Seat> seats = seatRepository.findAllById(seatIds);
        List<Seat> seats = seatRepository.findAllByIdForUpdate(seatIds);

        // 좌석이 없거나, 상태가 AVAILABLE이 아닌 좌석이 있는지 확인
        for (Seat seat : seats) {
            if (seat == null) {
                throw new ApiException(ErrorCode.E404, LogLevel.INFO, "Seat not found");
            }
            if (!seat.getStatus().equals(SeatStatus.AVAILABLE)) {
                throw new ApiException(ErrorCode.E002, LogLevel.INFO, "Seat not available");
            }
        }

        // 3. 예약 저장
        Reservation reservation = new Reservation(user);
        Reservation savedReservation = reservationRepository.save(reservation); // 예약 먼저 저장

        for (Seat seat : seats) {
            // 좌석 상태 변경
            seat.setStatus(SeatStatus.RESERVED);
            seatRepository.save(seat);

            // 예약 항목 저장
            ReservationItem reservationItem = new ReservationItem();
            reservationItem.setReservation(savedReservation);
            reservationItem.setSeat(seat);
            reservationItem.setSeatPrice(seat.getSeatPrice());

            ReservationItem saveReservationItem = reservationItemRepository.save(reservationItem);
            savedReservation.addItem(saveReservationItem);
        }

        return savedReservation;
    }
}
