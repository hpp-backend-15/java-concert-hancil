package io.hhplus.javaconcerthancil.support;

import io.hhplus.javaconcerthancil.domain.concert.*;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        createDummyConcertData();
    }

    private void createDummyConcertData() {

        String[] userNames = {"JHC", "MMA", "CLIMBING", "MAN"};

        for(String name : userNames) {
            User user = new User(name);
            userRepository.save(user);
        }

        // 콘서트 생성
        Concert concert = new Concert(null, "크러쉬 콘서트", "크러쉬의 라이브 콘서트입니다.");
        concertRepository.save(concert);

        // 콘서트 일정 생성
        List<ConcertSchedule> schedules = new ArrayList<>();
        LocalDateTime reservationAvailableAt = LocalDateTime.of(2024, 11, 1, 10, 0);
        LocalDateTime concertAt1 = LocalDateTime.of(2024, 12, 24, 19, 0);
        LocalDateTime concertAt2 = LocalDateTime.of(2024, 12, 25, 19, 0);

        ConcertSchedule schedule1 = new ConcertSchedule(null, reservationAvailableAt, concertAt1);
        ConcertSchedule schedule2 = new ConcertSchedule(null, reservationAvailableAt, concertAt2);

        // 스케줄을 콘서트에 연결
        schedule1.setConcert(concert);
        schedule2.setConcert(concert);

        schedules.add(schedule1);
        schedules.add(schedule2);

        // 콘서트 일정 저장
        scheduleRepository.saveAll(schedules);

        // 좌석 생성 (각 스케줄마다 50개씩)
        for (ConcertSchedule schedule : schedules) {
            for (int i = 1; i <= 50; i++) {
                Seat seat = new Seat(null, i, SeatStatus.AVAILABLE, 10000);
                seat.setConcertSchedule(schedule);
                seatRepository.save(seat);
            }
        }
    }
}
