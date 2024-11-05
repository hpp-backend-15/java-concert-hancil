package io.hhplus.javaconcerthancil.support;

import io.hhplus.javaconcerthancil.domain.concert.*;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.domain.user.UserWithVersion;
import io.hhplus.javaconcerthancil.domain.user.UserWithVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final UserWithVersionRepository userWithVersionRepository;

    @Override
    public void run(String... args) throws Exception {
        createDummyConcertData();
    }

    private void createDummyConcertData() {

        String[] userNames = {"JHC", "MMA", "CLIMBING", "MAN"};

        for(String name : userNames) {
            User user = new User(name);
            UserWithVersion userWithVersion = new UserWithVersion(name);
            userRepository.save(user);
            userWithVersionRepository.save(userWithVersion);
        }

        // 콘서트 생성
        Concert concert = new Concert("크러쉬 콘서트", "크러쉬의 라이브 콘서트입니다.");


        // 콘서트 일정 생성
        ConcertSchedule concertSchedule1 = new ConcertSchedule(
                LocalDateTime.of(2024,10,1,10,0),
                LocalDateTime.of(2024,12,23,20,0)
        );
        ConcertSchedule concertSchedule2 = new ConcertSchedule(
                LocalDateTime.of(2024,10,1,10,0),
                LocalDateTime.of(2024,12,24,20,0)
        );
        ConcertSchedule concertSchedule3 = new ConcertSchedule(
                LocalDateTime.of(2024,10,1,10,0),
                LocalDateTime.of(2024,12,25,20,0)
        );

        // 스케줄을 콘서트에 연결
        concert.addSchedule(concertSchedule1);
        concert.addSchedule(concertSchedule2);
        concert.addSchedule(concertSchedule3);
        concertRepository.save(concert);
    }
}
