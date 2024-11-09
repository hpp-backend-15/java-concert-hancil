package io.hhplus.javaconcerthancil.infrastructure.persistence.concert;

import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id IN :seatIds")
    Optional<List<Seat>> findAllByIdForUpdate(List<Long> seatIds);

    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.status = :status WHERE s.concertSchedule.id = :scheduleId")
    int updateSeatStatusByScheduleId(@Param("status") SeatStatus status, @Param("scheduleId") Long scheduleId);

    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.status = :status WHERE s.id IN :seatIds")
    int updateSeatStatusBySeatIds(@Param("status") SeatStatus status, @Param("seatIds") List<Long> seatIds);

}
