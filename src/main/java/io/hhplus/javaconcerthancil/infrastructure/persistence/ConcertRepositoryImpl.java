package io.hhplus.javaconcerthancil.infrastructure.persistence;

import io.hhplus.javaconcerthancil.domain.concert.Concert;
import io.hhplus.javaconcerthancil.domain.concert.ConcertRepository;
import io.hhplus.javaconcerthancil.domain.concert.ConcertSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertScheduleRedisRepository concertScheduleRedisRepository;

    @Override
    public Concert getConcert(long concertId) {

        Concert cachedConcert = concertScheduleRedisRepository.getConcert(concertId);
        if (cachedConcert == null) {
            Concert foundConcert = concertJpaRepository.findById(concertId)
                    .orElseThrow(() -> new IllegalArgumentException("concert not found"));
            concertScheduleRedisRepository.saveConcert(concertId, foundConcert);
            return foundConcert;
        } else {
            return cachedConcert;
        }
    }

    @Override
    public ConcertSchedule getConcertSeats(Long scheduleId) {
        return concertScheduleJpaRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("ConcertSchedule not found"));
    }
}
