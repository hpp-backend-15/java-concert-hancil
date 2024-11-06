package io.hhplus.javaconcerthancil.infrastructure.persistence;

import io.hhplus.javaconcerthancil.domain.concert.ConcertSchedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM concert_schedule e WHERE e.id = :id")
    Optional<Object> findByIdWithLock(@Param("id") Long scheduleId);
}
