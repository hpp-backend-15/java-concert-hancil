package io.hhplus.javaconcerthancil.domain.reservation;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.status = :status WHERE r.id = :reservationId")
    void updateReservationStatus(@Param("reservationId") long reservationId, @Param("status")ReservationStatus reservationStatus);
}
