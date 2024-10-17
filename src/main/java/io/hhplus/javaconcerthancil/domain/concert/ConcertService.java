package io.hhplus.javaconcerthancil.domain.concert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;

    public Concert getScheduledConcert(Long concertId) {
        return concertRepository.findById(concertId).orElseThrow(() -> new IllegalArgumentException("concert not found"));
    }

    public List<Seat> getConcertSeats(Long concertId, Long scheduleId) {

        // 1. Concert 엔티티 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("Concert not found"));

        // 2. ConcertSchedule 엔티티 조회
        ConcertSchedule concertSchedule = concertScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("ConcertSchedule not found"));

        // 3. 좌석 목록 조회
        return concertSchedule.getSeats();

    }

    public void changeSeatStatus(Long scheduledId, List<Long> seatIds) {

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found"));

            if (!seat.getStatus().equals(SeatStatus.AVAILABLE)) {
                throw new RuntimeException("Seat not available");
            }

            // 좌석 상태 변경
            seat.setStatus(SeatStatus.RESERVED);
            seatRepository.save(seat);
        }
    }
}
