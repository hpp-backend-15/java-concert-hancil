package io.hhplus.javaconcerthancil.integration.infrastructure;

import io.hhplus.javaconcerthancil.infrastructure.persistence.concert.ConcertScheduleRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConcertScheduleRedisRepositoryTest {

    @Autowired
    private ConcertScheduleRedisRepository concertScheduleRedisRepository;

}
