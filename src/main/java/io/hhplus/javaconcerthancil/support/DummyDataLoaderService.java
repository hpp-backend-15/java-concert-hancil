package io.hhplus.javaconcerthancil.support;

import io.hhplus.javaconcerthancil.domain.concert.*;
import io.hhplus.javaconcerthancil.domain.user.User;
import io.hhplus.javaconcerthancil.domain.user.UserRepository;
import io.hhplus.javaconcerthancil.domain.user.UserWithVersion;
import io.hhplus.javaconcerthancil.domain.user.UserWithVersionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DummyDataLoaderService {

    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final UserWithVersionRepository userWithVersionRepository;

    public void loadDummyData(){
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
        Concert concert = new Concert("Crush콘서트", "크러쉬의 라이브 콘서트입니다.");
        concertRepository.save(concert);

        // 콘서트 일정 생성
        List<ConcertSchedule> schedules = new ArrayList<>();
        LocalDateTime reservationAvailableAt = LocalDateTime.of(2024, 10, 1, 10, 0);
        LocalDateTime concertAt1 = LocalDateTime.of(2024, 12, 24, 19, 0);
        LocalDateTime concertAt2 = LocalDateTime.of(2024, 12, 25, 19, 0);

        ConcertSchedule schedule1 = new ConcertSchedule(reservationAvailableAt, concertAt1);
        ConcertSchedule schedule2 = new ConcertSchedule(reservationAvailableAt, concertAt2);

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
