package io.hhplus.javaconcerthancil.infrastructure.persistence.reservation;

import io.hhplus.javaconcerthancil.domain.reservation.Reservation;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationRepository;
import io.hhplus.javaconcerthancil.domain.reservation.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public void updateReservationStatus(long reservationId, ReservationStatus reservationStatus) {
        reservationJpaRepository.updateReservationStatus(reservationId, reservationStatus);
    }

    @Override
    public List<Reservation> updateExpiredReservations(ReservationStatus reservationStatus, ReservationStatus reservationStatus1, LocalDateTime expirationTime) {
        return reservationJpaRepository.updateExpiredReservations(reservationStatus, reservationStatus1, expirationTime);
    }

    @Override
    public List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime expirationTime) {
        return reservationJpaRepository.findByStatusAndCreatedAtBefore(status, expirationTime);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }
}
