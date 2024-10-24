package io.hhplus.javaconcerthancil.domain.payments;

import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentRepository paymentRepository;

    public Payment findPaymentsByReservationId(Long reservationId){
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(()-> new ApiException(ErrorCode.E404, LogLevel.INFO, "예약건이 존재하지 않습니다."));
        return payment;
    }

    public Payment completePayment(Payment payment, long amount){
        payment.setPaymentAt(LocalDateTime.now());
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.COMPLETED);
        return paymentRepository.save(payment);
    }

}
