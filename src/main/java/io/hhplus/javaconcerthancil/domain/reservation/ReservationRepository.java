package io.hhplus.javaconcerthancil.domain.reservation;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.status = :status WHERE r.id = :reservationId")
    void updateReservationStatus(@Param("reservationId") long reservationId, @Param("status")ReservationStatus reservationStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.status = :newStatus WHERE r.status = :oldStatus AND r.reservationAt < :expiredAt")
    List<Reservation> updateExpiredReservations(
            @Param("oldStatus") ReservationStatus reservationStatus,
            @Param("newStatus") ReservationStatus reservationStatus1,
            @Param("expiredAt") LocalDateTime expirationTime);

    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.reservationAt < :expirationTime")
    List<Reservation> findByStatusAndCreatedAtBefore(
            @Param("status") ReservationStatus status,
            @Param("expirationTime") LocalDateTime expirationTime);
}
