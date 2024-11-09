package io.hhplus.javaconcerthancil.domain.concert;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {

    List<Seat> findAllByIdForUpdate(List<Long> seatIds);
    int updateSeatStatusByScheduleId(SeatStatus status, Long scheduleId);
    int updateSeatStatusBySeatIds(SeatStatus status, List<Long> seatIds);
    void save(Seat seat);
    Seat findById(Long seatId);
}
