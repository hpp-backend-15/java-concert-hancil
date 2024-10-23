package io.hhplus.javaconcerthancil.unit.domain.payments;

import io.hhplus.javaconcerthancil.domain.payments.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
