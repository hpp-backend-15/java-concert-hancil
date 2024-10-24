package io.hhplus.javaconcerthancil.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long> {

    @Query("SELECT SUM(ri.seatPrice) FROM ReservationItem ri WHERE ri.reservation.id = :reservationId")
    Optional<Integer> findTotalSeatPriceByReservationId(@Param("reservationId") long reservationId);

    @Query("SELECT ri.seat.id FROM ReservationItem ri WHERE ri.reservation.id = :reservationId")
    List<Long> findSeatIdsByReservationId(@Param("reservationId") Long reservationIdz);
}
