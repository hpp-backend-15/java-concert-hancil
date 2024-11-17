package io.hhplus.javaconcerthancil.domain.payments;

public interface PaymentRepository {

    Payment findByReservationId(Long reservation_id);
    Payment save(Payment payment);
}
