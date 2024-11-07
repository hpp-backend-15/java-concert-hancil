package io.hhplus.javaconcerthancil.domain.concert;

import io.hhplus.javaconcerthancil.infrastructure.persistence.ConcertJpaRepository;
import io.hhplus.javaconcerthancil.infrastructure.persistence.ConcertScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

//    private final ConcertJpaRepository concertJpaRepository;
//    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final ConcertRepository concertRepository;

    public Concert getScheduledConcert(Long concertId) {
//        return concertJpaRepository.findById(concertId).orElseThrow(() -> new IllegalArgumentException("concert not found"));
        return concertRepository.getConcert(concertId);
    }

    public List<Seat> getConcertSeats(Long concertId, Long scheduleId) {

        // 1. ConcertSchedule 엔티티 조회
//        ConcertSchedule concertSchedule = concertScheduleJpaRepository.findById(scheduleId)
//                .orElseThrow(() -> new IllegalArgumentException("ConcertSchedule not found"));
        ConcertSchedule concertSchedule = concertRepository.getConcertSeats(scheduleId);

        // 2. 콘서트 예약가능여부 및 concertId 유효성 검사
        concertSchedule.isValid(concertId);

        // 3. 좌석 목록 조회
        return concertSchedule.getSeats();

    }

}
