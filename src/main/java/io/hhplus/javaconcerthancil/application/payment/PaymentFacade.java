package io.hhplus.javaconcerthancil.application.payment;

import io.hhplus.javaconcerthancil.domain.payments.Payment;
import io.hhplus.javaconcerthancil.domain.payments.PaymentsService;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationItemRepository;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationService;
import io.hhplus.javaconcerthancil.domain.user.*;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import io.hhplus.javaconcerthancil.interfaces.api.v1.payment.request.PaymentsRequest;
import io.hhplus.javaconcerthancil.interfaces.api.v1.payment.response.PaymentsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentsService paymentsService;
    private final UserService userService;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final ReservationItemRepository reservationItemRepository;

    public PaymentsResponse completePayment(Long userId, PaymentsRequest requestBody) {

        Payment paymentsByReservationId = paymentsService.findPaymentsByReservationId(requestBody.reservationId());
        Optional<Integer> totalSeatPriceByReservationId = reservationItemRepository.findTotalSeatPriceByReservationId(requestBody.reservationId());


        //3. 사용자 포인트 사용
        User user = userService.findById(userId).orElseThrow(
                ()-> new ApiException(ErrorCode.E404, LogLevel.INFO, "user not found")
        );

        user.subtractAmount(totalSeatPriceByReservationId.get());
        User updateBalanceUser = userService.updateBalance(user);

        BalanceHistory balanceHistory = new BalanceHistory(
                updateBalanceUser,
                totalSeatPriceByReservationId.get(),
                TransactionType.USE
        );

        userService.saveHistory(balanceHistory);

        Payment payment = paymentsService.completePayment(paymentsByReservationId, totalSeatPriceByReservationId.get());


        return new PaymentsResponse(payment.getId(),payment.getAmount(),payment.getStatus());
    }
}
