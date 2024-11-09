package io.hhplus.javaconcerthancil.application.payment;

import io.hhplus.javaconcerthancil.domain.concert.SeatRepository;
import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
import io.hhplus.javaconcerthancil.domain.payments.Payment;
import io.hhplus.javaconcerthancil.domain.payments.PaymentsService;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationItemRepository;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationRepository;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationStatus;
import io.hhplus.javaconcerthancil.domain.user.*;
import io.hhplus.javaconcerthancil.domain.waitingqueue.WaitingQueueService;
import io.hhplus.javaconcerthancil.infrastructure.persistence.waitingqueue.QueueTokenRedisRepository;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import io.hhplus.javaconcerthancil.interfaces.api.v1.payment.request.PaymentsRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.payment.response.PaymentsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentsService paymentsService;
    private final UserService userService;
    private final WaitingQueueService queueService;
    private final ReservationItemRepository reservationItemRepository;
    private final SeatRepository seatRepository;
//    private final WaitingQueueRepository waitingQueueRepository;
    private final ReservationRepository reservationRepository;
    private final QueueTokenRedisRepository queueTokenRedisRepository;


    public PaymentsResponse completePayment(Long userId, PaymentsRequest requestBody) {

        Payment paymentsByReservationId = paymentsService.findPaymentsByReservationId(requestBody.reservationId());
        Optional<Integer> totalSeatPriceByReservationId = reservationItemRepository.findTotalSeatPriceByReservationId(requestBody.reservationId());

        //3. 사용자 포인트 사용
        UserWithVersion user = userService.findByIdWithVersion(userId).orElseThrow(
                ()-> new ApiException(ErrorCode.E404, LogLevel.INFO, "user not found")
        );

        user.subtractAmount(totalSeatPriceByReservationId.get());
        userService.updateBalanceWithVersion(user);
//        User updateBalanceUser = userService.updateBalance(user);
//        BalanceHistory balanceHistory = new BalanceHistory(
//                updateBalanceUser,
//                totalSeatPriceByReservationId.get(),
//                TransactionType.USE
//        );
//
//        userService.saveHistory(balanceHistory);

        //4. 결제완료
        Payment payment = paymentsService.completePayment(paymentsByReservationId, totalSeatPriceByReservationId.get());

        //5. 좌석 완료
        List<Long> seatIds = reservationItemRepository.findSeatIdsByReservationId(requestBody.reservationId());
        seatRepository.updateSeatStatusBySeatIds(SeatStatus.OCCUPIED, seatIds);

        //6. 예약 완료
        //FIXME
        // Transaction의 범위를 고려해야하는 부분 중 하나 발견
        // 상태를 변경하고 step7 토큰 만료를 업데이트 하는데 예외가 발생하여도 롤백하지 않는다.
        // 그렇다는 얘기는 Seat의 상태도 롤백이 안되었을 것이고
        // 결제는 데이터는 완료가 되었을 것이고
        // 사용자의 포인트는 차감이 되었을 텐데
        // db를 확인해보니 예상과 일치
        reservationRepository.updateReservationStatus(requestBody.reservationId(), ReservationStatus.CONFIRMED);

        //7. 토큰 만료
        //TODO
        // 우선 결제를 완료했으면 토큰을 만료시키기위해 활성화 토큰을 제거하는 작업 부터 진행하자.
        // PaymentFacade에서 TOKEN을 직접 받아와야할 거같은데...
        // 설계까 틀어지므로 우선 임시로 TOKEN을 String으로 직접 만들어서 전달
        String token = "WAITING_" + "user:" + userId;
        queueTokenRedisRepository.deleteActiveToken(token);


        return new PaymentsResponse(payment.getId(),payment.getAmount(),payment.getStatus());
    }
}
