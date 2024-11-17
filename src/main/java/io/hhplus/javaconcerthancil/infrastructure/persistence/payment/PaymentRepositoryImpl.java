package io.hhplus.javaconcerthancil.infrastructure.persistence.payment;

import io.hhplus.javaconcerthancil.domain.payments.Payment;
import io.hhplus.javaconcerthancil.domain.payments.PaymentRepository;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment findByReservationId(Long reservation_id) {
        return paymentJpaRepository.findByReservationId(reservation_id)
                .orElseThrow(()-> new ApiException(ErrorCode.E404, LogLevel.INFO, "예약건이 존재하지 않습니다."));
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
