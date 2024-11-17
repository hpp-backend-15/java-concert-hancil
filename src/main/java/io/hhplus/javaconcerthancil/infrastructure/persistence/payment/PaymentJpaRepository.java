package io.hhplus.javaconcerthancil.infrastructure.persistence.payment;

import io.hhplus.javaconcerthancil.domain.payments.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(Long reservation_id);
}
