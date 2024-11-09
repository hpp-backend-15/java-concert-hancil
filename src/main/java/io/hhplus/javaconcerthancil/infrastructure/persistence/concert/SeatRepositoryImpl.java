package io.hhplus.javaconcerthancil.infrastructure.persistence.concert;

import io.hhplus.javaconcerthancil.domain.concert.Seat;
import io.hhplus.javaconcerthancil.domain.concert.SeatRepository;
import io.hhplus.javaconcerthancil.domain.concert.SeatStatus;
import io.hhplus.javaconcerthancil.interfaces.api.common.ApiException;
import io.hhplus.javaconcerthancil.interfaces.api.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {

    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Seat> findAllByIdForUpdate(List<Long> seatIds) {
        return seatJpaRepository.findAllByIdForUpdate(seatIds).orElseThrow(
                ()-> new ApiException(ErrorCode.E404, LogLevel.INFO, "Seat not found")
        );
    }

    @Override
    public int updateSeatStatusByScheduleId(SeatStatus status, Long scheduleId) {
        return seatJpaRepository.updateSeatStatusByScheduleId(status, scheduleId);
    }

    @Override
    public int updateSeatStatusBySeatIds(SeatStatus status, List<Long> seatIds) {
        return seatJpaRepository.updateSeatStatusBySeatIds(status, seatIds);
    }

    @Override
    public void save(Seat seat) {
        seatJpaRepository.save(seat);
    }

    @Override
    public Seat findById(Long seatId) {
        return seatJpaRepository.findById(seatId).orElseThrow(
                ()-> new ApiException(ErrorCode.E404, LogLevel.INFO, "Seat not found")
        );
    }
}
