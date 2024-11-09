package io.hhplus.javaconcerthancil.domain.reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository  {

    void updateReservationStatus(
            long reservationId,
            ReservationStatus reservationStatus
    );

    List<Reservation> updateExpiredReservations(
            ReservationStatus reservationStatus,
            ReservationStatus reservationStatus1,
            LocalDateTime expirationTime
    );

    List<Reservation> findByStatusAndCreatedAtBefore(
            ReservationStatus status,
            LocalDateTime expirationTime
    );

    Reservation save(Reservation reservation);
}
