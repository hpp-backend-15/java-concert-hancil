package io.hhplus.javaconcerthancil.domain.payments;

import io.hhplus.javaconcerthancil.domain.reservation.Reservation;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation; // 해당 결제와 연관된 예약

    private LocalDateTime paymentAt; // 결제 시간

    private long amount; // 결제 금액

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // 결제 상태 (pending, completed, failed)

    // Constructors

    public Payment(Reservation reservation) {
        this.reservation = reservation;
        this.status = PaymentStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public LocalDateTime getPaymentAt() {
        return paymentAt;
    }

    public long getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setPaymentAt(LocalDateTime paymentAt) {
        this.paymentAt = paymentAt;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
