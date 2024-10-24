package io.hhplus.javaconcerthancil.interfaces.scheduler;

import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.domain.concert.SeatRepository;
import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
import io.hhplus.javaconcerthancil.domain.payments.Payment;
import io.hhplus.javaconcerthancil.domain.payments.PaymentRepository;
import io.hhplus.javaconcerthancil.domain.payments.PaymentStatus;
import io.hhplus.javaconcerthancil.domain.reservation.*;
import io.hhplus.javaconcerthancil.domain.waitingqueue.WaitingQueueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final WaitingQueueRepository waitingQueueRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationItemRepository reservationItemRepository;
    private final PaymentRepository paymentRepository;

    @Scheduled(fixedDelay = 5 * 1000)
    @Transactional
    public void enteringUserQueue() {
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = waitingQueueRepository.updateExpiredQueues(now);
        log.info("{}개의 대기열 항목이 EXPIRED 되었습니다.", updatedCount);
    }

    @Scheduled(fixedRate = 10 * 1000)
    @Transactional
    public void cancelExpiredPendingReservations() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(5);

        // PENDING 상태이면서 만료된 예약들을 가져옴
        List<Reservation> expiredReservations = reservationRepository.findByStatusAndCreatedAtBefore(
                ReservationStatus.PENDING, expirationTime);

        for (Reservation reservation : expiredReservations) {
            // 예약 상태를 CANCELLED로 변경
            reservation.setStatus(ReservationStatus.CANCELLED);

            // 결제 상태를 FAILED로 변경
            Optional<Payment> paymentByReservationId = paymentRepository.findByReservationId(reservation.getId());
            paymentByReservationId.ifPresent(payment -> {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment); // 결제 정보 저장
            });

            // 관련 좌석의 상태를 AVAILABLE로 변경
            List<Long> seatIdsByReservationId = reservationItemRepository.findSeatIdsByReservationId(reservation.getId());
            for (Long seatId : seatIdsByReservationId) {
                Optional<Seat> seatOptional = seatRepository.findById(seatId);
                seatOptional.ifPresent(seat -> {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seatRepository.save(seat); // 좌석 정보 저장
                });
            }

            // 예약 정보 저장 (상태 업데이트 반영)
            reservationRepository.save(reservation);
        }
        log.info("{}개의 예약건들이 만료되었습니다.",expiredReservations.size());
    }


}
