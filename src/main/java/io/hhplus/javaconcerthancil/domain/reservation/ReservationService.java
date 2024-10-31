package io.hhplus.javaconcerthancil.domain.reservation;

import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.domain.concert.SeatRepository;
import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
import io.hhplus.javaconcerthancil.domain.payments.Payment;
import io.hhplus.javaconcerthancil.domain.payments.PaymentRepository;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Reservation reserveConcert(Long userId, Long concertId, Long scheduleId, List<Long> seatIds) {

        log.info("[JHC]초기id: {}" , userId);
        // 1-1. 사용자 확인
//        User user = userRepository.findById(userId)
        User user = userRepository.findByIdWithLock(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.E404, LogLevel.INFO, "User not found"));

        // 1-2 예약 상태 초기화
        Reservation reservation = new Reservation(user);

        // 2. 좌석 유효성 검사
        List<Seat> seats = seatRepository.findAllByIdForUpdate(seatIds);

        // 좌석이 없거나, 상태가 AVAILABLE이 아닌 좌석이 있는지 확인
        for (Seat seat : seats) {
            if (seat == null) {
                throw new ApiException(ErrorCode.E404, LogLevel.INFO, "Seat not found");
            }
            if (!seat.isAvailable()) {
                throw new ApiException(ErrorCode.E002, LogLevel.INFO, "Seat not available");
            }

            seat.setStatus(SeatStatus.RESERVED);

            // 예약 아이템 생성 및 예약에 추가
            ReservationItem item = new ReservationItem();
            item.setSeat(seat);
            item.setSeatPrice(seat.getSeatPrice()); // 좌석 가격 설정
            reservation.addItem(item); // 예약에 아이템 추가
            seatRepository.save(seat);
        }

        Reservation savedReservation = reservationRepository.save(reservation); // 예약 먼저 저장

        log.info("[JHC]최종id: {}" , savedReservation.getUser().getId());
        paymentRepository.save(new Payment(savedReservation));
        return savedReservation;
    }
}
